/**
 * Copyright     2014     Renren.com
 * @author JunHan 
 *  All rights reserved.
 */
package com.unimelb.mobile.breakout.server.tools;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;

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

}
