package dev.sample.ssd.das.rest;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import dev.sample.ssd.das.utils.DBUtils;
import dev.sample.ssd.das.vos.UserVO;
import dev.sample.ssd.das.vos.UsersVO;


@Path("users")
public class Users {
	
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSADetails() {
    	System.out.println("In get users");
    	try {
			Connection conn = DBUtils.getConnection();
			try {
				Statement stmt = conn.createStatement();
				String query = DBUtils.getUsersQuery();
				ResultSet rs = stmt.executeQuery(query);
				UsersVO usersList = new UsersVO();

				while (rs.next()) {
					UserVO userVO = new UserVO();
					userVO.setValues(rs);
					usersList.addUser(userVO);
				}
				System.out.println("Returned users");
				return Response.ok(usersList.toJSONArray(), MediaType.APPLICATION_JSON).build();
			} catch (Exception e) {
				e.printStackTrace();
				return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build(); 
			}finally {
				conn.close();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build(); 
		}
    }
    
}