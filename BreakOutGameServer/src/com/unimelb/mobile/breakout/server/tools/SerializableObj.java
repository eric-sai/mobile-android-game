package com.unimelb.mobile.breakout.server.tools;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.List;

import com.unimelb.mobile.breakout.server.po.User;


public class SerializableObj implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public byte[] User2Bytes(User user) throws IOException{
		byte [] bytes=null;
        ByteArrayOutputStream baos= new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(user);
        oos.flush(); 
        oos.close();
        bytes=baos.toByteArray();
        baos.close();
        return bytes;
	}
	public byte[] List2Bytes(List<User> lst) throws IOException{
		byte [] bytes=null;
        ByteArrayOutputStream baos= new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(lst);
        oos.flush(); 
        oos.close();
        bytes=baos.toByteArray();
        baos.close();
        return bytes;
	}

}
