To run locally
mvn liberty:dev

To run locally on a container
mvn liberty:devc

Deploy on local container runtime
mvn clean install
docker build -t approval-service .
docker login
docker tag approval-service murali1806/approval-service
docker push murali1806/approval-service
docker run -d -p 9080:9080 --name approval-service approval-service

Using openShift registry
mvn clean install
oc new-app . --name=approval-service --strategy=docker
oc start-build approval-service --from-dir=.

oc logs -f bc/approval-service (it takes time)
oc expose svc/approval-service
oc get pods

oc get routes
Access the route

Clean-up:
oc delete deployment approval-service
oc delete bc approval-service
oc delete is approval-service
oc delete svc approval-service
oc delete route approval-service