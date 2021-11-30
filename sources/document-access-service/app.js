
var ibm = require('ibm-cos-sdk');
var util = require('util');
var express = require('express');
var fs = require('fs');
var async = require("async");

require('dotenv').config();
var bodyParser = require('body-parser');

const port = process.env.PORT || 3001;

const app = express();
const fileUpload = require('express-fileupload');

// enable files upload
app.use(fileUpload({
    createParentPath: true
}));


var cos_config = {
    endpoint: process.env.COS_ENDPOINT,
    apiKeyId: process.env.COS_API_KEY_ID,
    serviceInstanceId: process.env.COS_RESOURCE_INSTANCE_ID,
    // signatureVersion: 'iam' 
};

var cos = new ibm.S3(cos_config);  

app.get("/", (req, res) => {
    console.log("/");
    res.set('Access-Control-Allow-Origin', '*');
    res.json({ message: "Hello from Document Access Service!" });
  });

app.get("/create-bucket-kp", async function (req, res) {
	console.log("\nCreating Bucket in Cloud Object Storage - " + process.env.COS_BUCKET_NAME);
	var response = await createBucketKP(process.env.COS_BUCKET_NAME);
    res.send(response);
});

app.get("/get-buckets-list", async function (req, res) {
	console.log("\Listing Buckets in Cloud Object Storage - ");
	var response = await getBuckets();
    res.send(response);
});

app.get("/get-bucket-content", async function (req, res) {
	console.log("\Fetching Bucket Content in Cloud Object Storage - ");
	var response = await getBucketContents(process.env.COS_BUCKET_NAME);
    res.send(response);
});

app.post("/upload-file", async function (req, res) {
	console.log("\Upload content in Bucket in Cloud Object Storage - ");
    // console.log("Req: ", req);
    // console.log ("\n\n\nReq Headers: ", req.headers);
    console.log ("\n\n\nReq body: ", req.body);
    console.log ("\n\n\nReq file: ", req.files);
    var response = await multiPartUploadStream(process.env.COS_BUCKET_NAME, req.files.File);
    console.log("Response: ", response);
    console.log({'message': 'Upload Successful'});
});

app.get("/getFile/:fileName", async function (req, res) {
	console.log("\Fetching Bucket Content in Cloud Object Storage - ");
    console.log(req.params.fileName);
    res.set('Access-Control-Allow-Origin', '*');
	var response = await getItem(process.env.COS_BUCKET_NAME, req.params.fileName);
    res.send(response.Body);
});

app.use(bodyParser.urlencoded({ extended: false }))
app.use(bodyParser.json())

// Start server
app.listen(port, () => {
	console.log('Listening on http://localhost:' + port);
});

//COS SDK APIs

function createBucketKP(bucketName) {
    console.log(`Creating new encrypted bucket: ${bucketName}`);
    return cos.createBucket({
        Bucket: bucketName,
        CreateBucketConfiguration: {
          LocationConstraint: process.env.COS_BUCKET_LOCATION
        },
        IBMSSEKPEncryptionAlgorithm: process.env.KP_ENCRYPTION_ALGORITHM,
        IBMSSEKPCustomerRootKeyCrn: process.env.KP_ROOT_KEY_CRN
    }).promise()
    .then((() => {
        console.log(`Bucket: ${bucketName} created!`);
        return ('Bucket ${bucketName} created !');
    }))
    .catch((e) => {
        console.error(`ERROR: ${e.code} - ${e.message}\n`);
        return ('Error in Bucket creation !');
    });
}

function createBucket(bucketName) {
    console.log(`Creating new bucket: ${bucketName}`);
    return cos.createBucket({
        Bucket: bucketName,
        CreateBucketConfiguration: {
          LocationConstraint: 'us-south-standard'
        },        
    }).promise()
    .then((() => {
        console.log(`Bucket: ${bucketName} created!`);
        return ('Bucket ${bucketName} created !');
    }))
    .catch((e) => {
        console.error(`ERROR: ${e.code} - ${e.message}\n`);
        return ('Error in Bucket ${bucketName} creation !');
    });
}

function getBuckets() {
    console.log('Retrieving list of buckets');
    return cos.listBuckets()
    .promise()
    .then((data) => {
        if (data.Buckets != null) {
            for (var i = 0; i < data.Buckets.length; i++) {
                console.log(`Bucket Name: ${data.Buckets[i].Name}`);
            }
        }
        return (data.Buckets);
    })
    .catch((e) => {
        console.error(`ERROR: ${e.code} - ${e.message}\n`);
    });
}

function getBucketContents(bucketName) {
    console.log(`Retrieving bucket contents from: ${bucketName}`);
    return cos.listObjects(
        {Bucket: bucketName},
    ).promise()
    .then((data) => {
        var bucketContent = [];
        if (data != null && data.Contents != null) {
            for (var i = 0; i < data.Contents.length; i++) {
                var itemKey = data.Contents[i].Key;
                var itemSize = data.Contents[i].Size;
                var content = { 'Item': itemKey, 'Item Size (bytes)': itemSize}
                // console.log(`Item: ${itemKey} (${itemSize} bytes).`)
                console.log (content);
                bucketContent.push(content); 
            }
        }
        return(bucketContent);
    })
    .catch((e) => {
        console.error(`ERROR: ${e.code} - ${e.message}\n`);
    });
}

async function multiPartUploadStream(bucketName, reqObj) {
    var uploadID = null;

    var itemName = reqObj.name;
    // var itemName = 'test-file';
    console.log(`Starting multi-part upload for ${itemName} to bucket: ${bucketName}`);
    return cos.createMultipartUpload({
        Bucket: bucketName,
        Key: itemName
    }).promise()
    .then((data) => {
        console.log("\nData:",data);
        uploadID = data.UploadId;

        fileData = reqObj.data;
        //begin the file upload        
        // fs.readFile(filePath, (e, fileData) => {
            //min 5MB part
            console.log("FILE DATA: ", fileData);
            var partSize = 1024 * 1024 * 5;
            var partCount = Math.ceil(fileData.length / partSize);
    
            async.timesSeries(partCount, (partNum, next) => {
                console.log('partNum:',partNum);
                var start = partNum * partSize;
                var end = Math.min(start + partSize, fileData.length);
    
                partNum++;

                console.log(`Uploading to ${itemName} (part ${partNum} of ${partCount})`);  

                cos.uploadPart({
                    Body: fileData.slice(start, end),
                    Bucket: bucketName,
                    Key: itemName,
                    PartNumber: partNum,
                    UploadId: uploadID
                }).promise()
                .then((data) => {
                    console.log(data);
                    next(null, {ETag: data.ETag, PartNumber: partNum});
                })
                .catch((e) => {
                    cancelMultiPartUpload(bucketName, itemName, uploadID);
                    console.error(`ERROR: ${e.code} - ${e.message}\n`);
                });
            }, (e, dataPacks) => {
                console.log("datapacks:", dataPacks);
                cos.completeMultipartUpload({
                    Bucket: bucketName,
                    Key: itemName,
                    MultipartUpload: {
                        Parts: dataPacks
                    },
                    UploadId: uploadID
                }).promise()
                .then( (res) => { 
                    console.log(`Upload of all ${partCount} parts of ${itemName} successful.`);
                    return ({'message': 'Upload Successful'});
                })
                .catch((e) => {
                    cancelMultiPartUpload(bucketName, itemName, uploadID);
                    console.error(`ERROR: ${e.code} - ${e.message}\n`);
                    return ({'message': 'Upload Failed'});
                });
            });
        // });
    })
    .catch((e) => {
        console.error(`ERROR: ${e.code} - ${e.message}\n`);
    });
}

function cancelMultiPartUpload(bucketName, itemName, uploadID) {
    return cos.abortMultipartUpload({
        Bucket: bucketName,
        Key: itemName,
        UploadId: uploadID
    }).promise()
    .then(() => {
        console.log(`Multi-part upload aborted for ${itemName}`);
    })
    .catch((e)=>{
        console.error(`ERROR: ${e.code} - ${e.message}\n`);
    });
}

async function multiPartUpload(bucketName, itemName, filePath) {
    var uploadID = null;

    if (!fs.existsSync(filePath)) {
        console.error(new Error(`The file \'${filePath}\' does not exist or is not accessible.`));
        return;
    }

    console.log(`Starting multi-part upload for ${itemName} to bucket: ${bucketName}`);
    return cos.createMultipartUpload({
        Bucket: bucketName,
        Key: itemName
    }).promise()
    .then((data) => {
        uploadID = data.UploadId;

        //begin the file upload        
        fs.readFile(filePath, (e, fileData) => {
            //min 5MB part
            console.log("FILE DATA: ", fileData);
            console.log("e:",e);
            var partSize = 1024 * 1024 * 5;
            var partCount = Math.ceil(fileData.length / partSize);
    
            async.timesSeries(partCount, (partNum, next) => {
                var start = partNum * partSize;
                var end = Math.min(start + partSize, fileData.length);
    
                partNum++;

                console.log(`Uploading to ${itemName} (part ${partNum} of ${partCount})`);  

                cos.uploadPart({
                    Body: fileData.slice(start, end),
                    Bucket: bucketName,
                    Key: itemName,
                    PartNumber: partNum,
                    UploadId: uploadID
                }).promise()
                .then((data) => {
                    next(e, {ETag: data.ETag, PartNumber: partNum});
                })
                .catch((e) => {
                    cancelMultiPartUpload(bucketName, itemName, uploadID);
                    console.error(`ERROR: ${e.code} - ${e.message}\n`);
                });
            }, (e, dataPacks) => {
                cos.completeMultipartUpload({
                    Bucket: bucketName,
                    Key: itemName,
                    MultipartUpload: {
                        Parts: dataPacks
                    },
                    UploadId: uploadID
                }).promise()
                .then( (res) => { 
                    console.log(`Upload of all ${partCount} parts of ${itemName} successful.`);
                    return ({'message': 'Upload Successful'});
                })
                .catch((e) => {
                    cancelMultiPartUpload(bucketName, itemName, uploadID);
                    console.error(`ERROR: ${e.code} - ${e.message}\n`);
                    return ({'message': 'Upload Failed'});
                });
            });
        });
    })
    .catch((e) => {
        console.error(`ERROR: ${e.code} - ${e.message}\n`);
    });
}

function getItem(bucketName, itemName) {
    console.log(`Retrieving item from bucket: ${bucketName}, key: ${itemName}`);
    return cos.getObject({
        Bucket: bucketName, 
        Key: itemName
    }).promise()
    .then((data) => {
        if (data != null) {
            // console.log('File Contents: ' + Buffer.from(data.Body).toString());
            console.log(data);
            return (data);
        }    
    })
    .catch((e) => {
        console.error(`ERROR: ${e.code} - ${e.message}\n`);
    });
}


// function cancelMultiPartUpload(bucketName, itemName, uploadID) {
//     return cos.abortMultipartUpload({
//         Bucket: bucketName,
//         Key: itemName,
//         UploadId: uploadID
//     }).promise()
//     .then(() => {
//         console.log(`Multi-part upload aborted for ${itemName}`);
//     })
//     .catch((e)=>{
//         console.error(`ERROR: ${e.code} - ${e.message}\n`);
//     });
// }