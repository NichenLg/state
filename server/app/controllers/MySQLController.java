package controllers;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.Parameter;
import javax.persistence.Query;

import jobs.UpdateJob;
import models.Result;
import play.Logger;
import play.Play;
import play.cache.Cache;
import play.db.jpa.JPA;
import play.mvc.*;
import util.CommonUtil;
import util.MySqlDBUtil;

public class MySQLController extends Controller {
    
    public static void getBases() {
    	List<Map<String, String>> hosts = CommonUtil.getDBs();
    	Result result = new Result();
    	
    	List<Map<String, Object>> infoList = new ArrayList<Map<String, Object>>();
    	
    	for (Map<String, String> host : hosts) {
			String mysqlHost = host.get("ip");
			Map<String, Object> infos = null;
	    	try {
	    		infos = (Map<String, Object>) Cache.get(UpdateJob.BASES_KEY +"_" +mysqlHost);
	    	}catch (Exception e) {
	    		Logger.error(e, "get bases from cache error", ""); 
	    	}
	    	
	    	if(infos == null){
	    		infos = new HashMap<String, Object>();
	    		infos.put("Error", "服务器连接断开");
	    	}
	    	infoList.add(infos);
    	}
    	
    	result.code = 200;
		result.msg = "成功";
    	result.data = infoList;
    	renderJSON(result);
    }
    
    public static void getCaches() {
    	List<Map<String, String>> hosts = CommonUtil.getDBs();
    	Result result = new Result();
    	
    	List<Map<String, Object>> infoList = new ArrayList<Map<String, Object>>();
    	
    	for (Map<String, String> host : hosts) {
			String mysqlHost = host.get("ip");
			Map<String, Object> infos = null;
	    	try {
	    		infos = (Map<String, Object>) Cache.get(UpdateJob.CACHE_KEY +"_" +mysqlHost);
	    	}catch (Exception e) {
	    		Logger.error(e, "get caches from cache error", ""); 
	    	}
	    	
	    	if(infos == null){
	    		infos = new HashMap<String, Object>();
	    		infos.put("Error", "服务器连接断开");
	    	}
	    	infoList.add(infos);
    	}
    	
    	result.code = 200;
		result.msg = "成功";
    	result.data = infoList;
    	renderJSON(result);
    }
    
    public static void getReplications() {
    	Result result = new Result();
    	Map<String, String> infos = null;
    	try {
    		infos = (Map<String, String>) Cache.get(UpdateJob.REPLICATION_KEY);
    		result.code = 200;
    		result.msg = "成功";
    	}catch (Exception e) {
    		result.code = 700;
    		result.msg = "获取数据失败";
    		Logger.error(e, "get bases from cache error", ""); 
    	}
    	
    	if(infos == null){
    		infos = new HashMap<String, String>();
    		infos.put("Error", "服务器连接断开");
    	}
    	
    	result.data = infos;
    	renderJSON(result);
    }
    
    private static void replicationColumns(){
    	List<Map<String, Object>> columnsList = new ArrayList<Map<String, Object>>();
		
		Map<String, Object> hostKeyMap = new HashMap<String, Object>();
		hostKeyMap.put("title", "主机");
		hostKeyMap.put("key", "host");
		columnsList.add(hostKeyMap);
		
		Map<String, Object> masterSlaveKeyMap = new HashMap<String, Object>();
		masterSlaveKeyMap.put("title", "主/从");
		masterSlaveKeyMap.put("key", "masterSlave");
		columnsList.add(masterSlaveKeyMap);
		
		Map<String, Object> stateSlaveKeyMap = new HashMap<String, Object>();
		stateSlaveKeyMap.put("title", "状态");
		stateSlaveKeyMap.put("key", "state");
		columnsList.add(stateSlaveKeyMap);
		
		Map<String, Object> relayKeyMap = new HashMap<String, Object>();
		relayKeyMap.put("title", "时延(s)");
		relayKeyMap.put("key", "relay");
		columnsList.add(relayKeyMap);
    }

}
