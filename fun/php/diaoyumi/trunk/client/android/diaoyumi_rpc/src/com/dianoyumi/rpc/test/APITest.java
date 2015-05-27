package com.dianoyumi.rpc.test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Date;

import org.junit.Test;

import com.dianoyumi.common.Tools;
import com.dianoyumi.rpc.API;
import com.dianoyumi.vo.Event;

public class APITest {
	
	private API api = new API("http://localhost/diaoyumi");
	private String testUser = "apitest" + Tools.time();
	private String testEmail = testUser + "@hotmail.com";
	private String testPassword = "abcdefg";
	private String testMobile = ("131" + Tools.time()).substring(0,10);
	
	

	@Test
	public void testRegister() {
		assertTrue(api.register(testEmail, testUser,testPassword, testMobile) > 0);
	}
	
	@Test
	public void testUniqueCheckEmail() {
		assertFalse(api.uniqueCheckEmail(testEmail));
		String newEmail = testUser + "@com.hotmail";
		assertTrue(api.uniqueCheckEmail(newEmail));
	}

	@Test
	public void testUniqueCheckName() {
		assertFalse(api.uniqueCheckName(testUser));
		String newUser = testUser + "nnn";
		assertTrue(api.uniqueCheckName(newUser));
	}


	@Test
	public void testAuthByEmail() {
		assertTrue(api.authByEmail(testEmail, testPassword + "nnn") == 0);
		assertTrue(api.authByEmail(testEmail, testPassword) > 0);
		
		
	}

	@Test
	public void testAuthByName() {
		assertTrue(api.authByName(testUser, testPassword + "nnn") == 0);
		assertTrue(api.authByName(testUser, testPassword) > 0);
	}

	@Test
	public void testChangePassword() {
		int userId = api.authByName(testUser, testPassword);
		assertTrue(api.changePassword(userId, testPassword +"new", testPassword));
		assertTrue(api.changePassword(userId, testPassword, testPassword + "new"));
		int newUserId = api.authByName(testUser, testPassword);
		assertTrue(userId == newUserId);
	}
	
	@Test
	public void testNewEvent(){
		int userId = api.authByName(testUser, testPassword);
		String newPlace = "api_test_place" + Tools.time();
		assertTrue(userId > 0);
		Event event = new Event();
		event.setRid(Tools.generateRID(Tools.RID_TYPE_FISHING));
		event.setEventTime(new Date());
		event.setType(Tools.RID_TYPE_FISHING);
		event.setLat(30.99);
		event.setLng(120.99);
		event.setPlace(newPlace);
		event.setNewPlace(true);
		event.setCompanion("api_test_companion");
		event.setPicture(Tools.generateRID(Tools.RID_TYPE_PICTURE) + ".png");
		event.setTitle("api_test_title");
		event.setPrice(999.99);
		event.setDesc("api_test_desc aaaaaaaaaaaaaaaaaaaaaaaa\r\naaaaaaaaaaaaaaaaaaaaaa\r\naaaaaaaaaaaaaaaaaaaaaaaa\r\naaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa end.");
		event.setProperties("api_test_properties bbbbbbbbbbbbbbbbb\r\nbbbbbbbbbbb\r\n end"); 
		event.setStatus(0);
		
		//先传图片
		assertTrue(api.uploadPicture(userId, event.getPicture(), "./test.png"));
		assertTrue(api.newEvent(event));
		
		assertTrue(api.getAllByUserId(1000000).size() == 0);
		ArrayList<Event> list = api.getAllByUserId(userId);
		assertTrue(list.size() == 1);
		assertTrue(list.get(0).equals(event));
		
		ArrayList<String> listPlace = api.getNearPalce(event.getLng(), event.getLat());
		assertTrue(listPlace.size() > 0);
		assertTrue(listPlace.contains(newPlace));
		
		assertTrue(api.deleteEvent(userId, event.getRid()));
		assertFalse(api.getNearPalce(event.getLng(), event.getLat()).contains(newPlace));
		
	}

}
