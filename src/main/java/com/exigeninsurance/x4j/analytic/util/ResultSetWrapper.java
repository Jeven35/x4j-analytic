/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/

package com.exigeninsurance.x4j.analytic.util;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import com.exigeninsurance.x4j.analytic.api.ReportException;


public class ResultSetWrapper implements Cursor {

    private ResultSet rs;
    private ResultSetMetaData metadata;

    private ResultSetWrapper(ResultSet rs) {
        this.rs = rs;
        try {
			this.metadata = rs.getMetaData();
		} catch (SQLException e) {
			throw new ReportException(e);
		}
    }

	public static Cursor wrap(ResultSet rs) {
		return new ResultSetWrapper(rs);
	}

    @Override
    public void close() {
        try {
            rs.close();
        } catch (SQLException e) {
            throw new ReportException(e);
        }
    }

    @Override
    public boolean next() {
        try {
            return rs.next();
        } catch (SQLException e) {
            throw new ReportException(e);
        }
    }

    @Override
    public CursorMetadata getMetadata() {
        try {
            return CursorMetadata.createFromResultSet(rs);
        } catch (SQLException e) {
            throw new ReportException(e);
        }
    }

    @Override
    public Object getObject(int i) {
        try {        	
            Object obj = rs.getObject(i);
            String name = metadata.getColumnTypeName(i);            
            return CursorMetadata.convert(obj, name) ;
        } catch (SQLException e) {
            throw new ReportException(e);
        }
    }

    @Override
    public void reset() {
        throw new UnsupportedOperationException("ResultSetWrapper is not scrollable");
    }

	@Override
	public boolean isClosed() {
		
		try {
			return rs.isClosed();
		} catch (SQLException e) {
			throw new ReportException(e);
		}
	}
}
