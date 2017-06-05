package org.lc.db.DAO;

import java.lang.reflect.Method;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.sql.DataSource;

import oracle.jdbc.OracleTypes;

import org.lc.reflect.ObjectPropertiesUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.CallableStatementCallback;
import org.springframework.jdbc.core.CallableStatementCreator;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class BaseDAO
{
	protected static Logger errorLogger = Logger.getLogger("org.lacare.provreq.dao.BaseDAO");

	private static DataSource dataSource;

	public static Connection getConnection() throws SQLException
	{
	    return getDataSource().getConnection();
	}

	public static void release(Connection connection)
	{
		try
		{
			if (connection != null)
			{
				connection.close();
			}
		}
		catch (SQLException ex)
		{
			errorLogger.log(Level.WARNING, "Error closing connection--" + ex.getMessage());
		}
	}

	public void setDataSource(DataSource dataSource)
	{
		this.dataSource = dataSource;
	}

	public static DataSource getDataSource()
	{
		return dataSource;
	}


	@Autowired
	private JdbcTemplate jdbcTemplate;

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate)
	{
		this.jdbcTemplate = jdbcTemplate;
	}

	public JdbcTemplate getJdbcTemplate()
	{
		return jdbcTemplate;
	}

	public static final String DEFAULT_DATE_FORMAT = "MM/dd/yyyy";
	public static final SimpleDateFormat DEFAULT_SIMPLE_DATE_FORMAT = new SimpleDateFormat(DEFAULT_DATE_FORMAT);
	public static final DecimalFormat DEFAULT_DECIMAL_FORMAT = new DecimalFormat();

	public static void setParams(PreparedStatement cs, Map<Integer, Object> params, Map<Integer, Class> paramClz) throws SQLException {
		if (cs==null || params==null) return;
		for (int i : params.keySet()) {
        	Object pval = params.get(i);
        	Class clz = paramClz!=null && paramClz.get(i)!=null ? paramClz.get(i) : null;
        	if (clz != null) {
        		if (clz.equals(String.class)) {
        			cs.setString(i, (String)pval);
        		} else if (clz.equals(Integer.class)) {
        			cs.setInt(i, (Integer)pval);
        		} else if (clz.equals(java.sql.Date.class)) {
        			cs.setDate(i, (java.sql.Date)pval);
        		} else {
        			cs.setObject(i, pval);
        		}
        	} else {
        		cs.setObject(i, pval);
        	}
        }
	}
	/*
	 * This is to be use with jquery datatable which takes input json data as a list of list of strings.
	 * The first of formats will be date format.
	 * The second of formats will be number format.
	 */
	public List<List<String>> getListOfList(final String stmn, final Map<Integer, Object> params, final Map<Integer, Class> paramClz, final int outIdx, final List<String> outFields, final String... formats) {
		List<List<String>> out = (List<List<String>>) jdbcTemplate.execute(
			     new CallableStatementCreator() {
			         public CallableStatement createCallableStatement(Connection con) throws SQLException {
			            CallableStatement cs = con.prepareCall(stmn);
			            cs.clearParameters();
			            setParams(cs, params, paramClz);
			            if (outIdx>0) cs.registerOutParameter(outIdx, OracleTypes.CURSOR);//
			            return cs;
			         }
			      }, new CallableStatementCallback() {
			         public Object doInCallableStatement(CallableStatement cs) throws SQLException,DataAccessException {
			     		SimpleDateFormat dateFormat = DEFAULT_SIMPLE_DATE_FORMAT;
			    		if (formats!=null && formats.length>0) {
			    			try {
			    				dateFormat = new SimpleDateFormat(formats[0]);
			    			} catch(Exception e) {
			    				dateFormat = DEFAULT_SIMPLE_DATE_FORMAT;
			    			}
			    		}
			    		DecimalFormat decimalFormat = DEFAULT_DECIMAL_FORMAT;
			    		if (formats!=null && formats.length>1) {
			    			try {
			    				decimalFormat = new DecimalFormat(formats[1]);
			    			} catch(Exception e) {
			    				decimalFormat = DEFAULT_DECIMAL_FORMAT;
			    			}
			    		}
			            List<List<String>> resultsList = new ArrayList<List<String>>();
			            if (outIdx<1) return resultsList;
			            cs.execute();
			            ResultSet rs = (ResultSet) cs.getObject(outIdx);//
			            while (rs.next()) {
			               List row = new ArrayList<String>();
			               for (int i=0; i<outFields.size(); i++) {
			            	   Object field = rs.getObject(outFields.get(i));
			            	   if (field != null) {
			            		   if (field instanceof String) {
			            			   row.add((String)field);
			            		   } else if (field instanceof Integer) {
			            			   row.add(decimalFormat.format((Integer)field));
			            		   } else if (field instanceof Date) {
			            			   row.add(dateFormat.format((Date)field));
			            		   } else {
			            			   row.add(field.toString());
			            		   }
			            	   } else {
			            		   row.add("");
			            	   }
			               }
			               resultsList.add(row);
			            }
			            rs.close();
			            return resultsList;
			         }
			   });

		return out;
	}

	public List<List<String>> getListOfList(final String stmn, IParamsMap iparams, IParamClzMap iparamclz, final int outIdx, IOutFields ioutFields, final String... formats) {
		final Map<Integer, Object> params = iparams.getParams();
		final List<String> outfields = ioutFields.getOutFields();
		final Map<Integer, Class> paramclz = iparamclz.getParams();
		return getListOfList(stmn, params, paramclz, outIdx, outfields, formats);
	}

	public String getHTMLTable(final String stmn, final Map<Integer, Object> params, final Map<Integer, Class> paramClz, final int outIdx, final List<String> outFields, final List<String> colheaders, final String... formats) {
		return (String) jdbcTemplate.execute(
			     new CallableStatementCreator() {
			         public CallableStatement createCallableStatement(Connection con) throws SQLException {
			            CallableStatement cs = con.prepareCall(stmn);
			            cs.clearParameters();
			            setParams(cs, params, paramClz);
			            if (outIdx>0) cs.registerOutParameter(outIdx, OracleTypes.CURSOR);//
			            return cs;
			         }
			      }, new CallableStatementCallback() {
			         public Object doInCallableStatement(CallableStatement cs) throws SQLException,DataAccessException {
			     		SimpleDateFormat dateFormat = DEFAULT_SIMPLE_DATE_FORMAT;
			    		if (formats!=null && formats.length>0) {
			    			try {
			    				dateFormat = new SimpleDateFormat(formats[0]);
			    			} catch(Exception e) {
			    				dateFormat = DEFAULT_SIMPLE_DATE_FORMAT;
			    			}
			    		}
			    		DecimalFormat decimalFormat = DEFAULT_DECIMAL_FORMAT;
			    		if (formats!=null && formats.length>1) {
			    			try {
			    				decimalFormat = new DecimalFormat(formats[1]);
			    			} catch(Exception e) {
			    				decimalFormat = DEFAULT_DECIMAL_FORMAT;
			    			}
			    		}
			            String out = "<table><thead><tr>";
			            for (int h = 0; h<colheaders.size(); h++) {
			            	out += "<td>" + colheaders.get(h) + "</td>";
			            }
			            out += "</tr></thead><tbody>";
			            if (outIdx<1) {
			            	out += "</tbody></table>";
			            	return out;
			            }
			            cs.execute();
			            ResultSet rs = (ResultSet) cs.getObject(outIdx);//
			            while (rs.next()) {
			               out += "<tr>";
			               for (int i=0; i<outFields.size(); i++) {
			            	   Object field = rs.getObject(outFields.get(i));
			            	   String val = "";
			            	   if (field != null) {
			            		   if (field instanceof String) {
			            			   val = ((String)field);
			            		   } else if (field instanceof Integer) {
			            			   val = decimalFormat.format((Integer)field);
			            		   } else if (field instanceof Date) {
			            			   val = dateFormat.format((Date)field);
			            		   } else {
			            			   val = field.toString();
			            		   }
			            	   }
			            	   out += "<td align='left' nowrap>" + val + "</td>";
			               }
			               out+="</tr>";
			            }
			            rs.close();
			            out += "</tbody></table>";
			            return out;
			         }
			   });
	}

	public String getHTMLTable(final String stmn, IParamsMap iparams, IParamClzMap iparamclz, final int outIdx, IOutFields ioutfields, IColumnHeaders icolheaders, final String... formats) {
		final Map<Integer, Object> params = iparams.getParams();
		final List<String> outfields = ioutfields.getOutFields();
		final List<String> colheaders = icolheaders.getColumnHeaders();
		final Map<Integer, Class> paramclz = iparamclz.getParams();
		return getHTMLTable(stmn, params, paramclz, outIdx, outfields, colheaders, formats);
	}

	public List getList(final String stmn, final Map<Integer, Object> params, final Map<Integer, Class> paramClz, final int outIdx, final IObjectMapper mapper) {
		List out = (List) jdbcTemplate.execute(
			     new CallableStatementCreator() {
			         public CallableStatement createCallableStatement(Connection con) throws SQLException {
			            CallableStatement cs = con.prepareCall(stmn);
			            cs.clearParameters();
			            setParams(cs, params, paramClz);
			            if (outIdx>0) cs.registerOutParameter(outIdx, OracleTypes.CURSOR);//
			            return cs;
			         }
			      }, new CallableStatementCallback() {
			         public Object doInCallableStatement(CallableStatement cs) throws SQLException,DataAccessException {
			            List resultsList = new ArrayList();
			            if (outIdx<1) return resultsList;
			            cs.execute();
			            ResultSet rs = (ResultSet) cs.getObject(outIdx);//
			            while (rs.next()) {
			               Object tmp = mapper.convert(rs);
			               if (tmp != null)
			            	   resultsList.add(tmp);
			            }
			            rs.close();
			            return resultsList;
			         }
			   });

		return out;
	}

	public List getList(final String stmn, IParamsMap iparams, IParamClzMap iparamclz, final int outIdx, final IObjectMapper mapper) {
		final Map<Integer, Object> params = iparams.getParams();
		final Map<Integer, Class> paramclz = iparamclz.getParams();
		return getList(stmn, params, paramclz, outIdx, mapper);
	}

	public Object[] getArray(final String stmn, final Map<Integer, Object> params, final Map<Integer, Class> paramClz, final int outIdx, final IObjectMapper mapper) {
		Object[] out = (Object[]) jdbcTemplate.execute(
			     new CallableStatementCreator() {
			         public CallableStatement createCallableStatement(Connection con) throws SQLException {
			            CallableStatement cs = con.prepareCall(stmn);
			            cs.clearParameters();
			            setParams(cs, params, paramClz);
			            if (outIdx>0) cs.registerOutParameter(outIdx, OracleTypes.CURSOR);//
			            return cs;
			         }
			      }, new CallableStatementCallback() {
			         public Object doInCallableStatement(CallableStatement cs) throws SQLException,DataAccessException {
			            cs.execute();
			            if (outIdx<1) return new Object[0];
			            ResultSet rs = (ResultSet) cs.getObject(outIdx);
			        	List resultsList = new ArrayList();
			        	int idx = 0;
			            while (rs.next()) {
			            	Object obj = mapper.convert(rs);
			            	if (obj!=null) resultsList.add(obj);
			            }
			            rs.close();
			            Object[] resultsArray = new Object[resultsList.size()];
			            for (int ar = 0; ar<resultsArray.length; ar++) {
			            	resultsArray[ar] = resultsList.get(ar);
			            }
			            return resultsArray;
			         }
			   });

		return out;
	}

	public Object[] getArray(final String stmn, IParamsMap iparams, IParamClzMap iparamclz, final int outIdx, final IObjectMapper mapper) {
		final Map<Integer, Object> params = iparams.getParams();
		final Map<Integer, Class> paramclz = iparamclz.getParams();
		return getArray(stmn, params, paramclz, outIdx, mapper);
	}

	public Object getObject(final String stmn, final Map<Integer, Object> params, final Map<Integer, Class> paramClz, final int outIdx, final int outtype, 
			final IObjectMapper mapper, final ITemplateCallback... callbacks) {
				Object out = jdbcTemplate.execute(
			     new CallableStatementCreator() {
			         public CallableStatement createCallableStatement(Connection con) throws SQLException {
			            CallableStatement cs = con.prepareCall(stmn);
			            cs.clearParameters();
			            setParams(cs, params, paramClz);
			            if (outIdx>0) cs.registerOutParameter(outIdx, outtype);
			            if (callbacks != null && callbacks.length > 0)
			            	for (ITemplateCallback callback : callbacks) {
					            try {
					            	if (callback.getType() == TemplateCallbackType.TypeCreate) {
					            		callback.exec(cs);
					            	}
					            } catch(Exception e) {
					            	e.printStackTrace();
					            }
			         		}
			            return cs;
			         }
			      }, new CallableStatementCallback() {
			         public Object doInCallableStatement(CallableStatement cs) throws SQLException,DataAccessException {
			            cs.execute();
			            if (callbacks != null && callbacks.length > 0)
			            	for (ITemplateCallback callback : callbacks) {
					            try {
					            	if (callback.getType() == TemplateCallbackType.TypeExecute) {
					            		callback.exec(cs);
					            	}
					            } catch(Exception e) {
					            	e.printStackTrace();
					            }
			         		}			            
			            if (outIdx<1) return null;
			            if (outtype == OracleTypes.CURSOR) {
				            ResultSet rs = (ResultSet) cs.getObject(outIdx);
				        	Object ret = null;
				            if (rs.next() && mapper!=null) {
				               ret = mapper.convert(rs);
				            }
				            rs.close();
				            return ret;
			            } else {
			            	Object tmp = cs.getObject(outIdx);
			            	if (PATTERN_ORAERR.matcher(tmp.toString()).find())
			            		throw new SQLException(tmp.toString());
			            	return tmp;
			            }

			         }
			   });

		return out;
	}

	public Object getObject(final String stmn, IParamsMap iparams, IParamClzMap iparamclz, final int outIdx, final int outtype, final IObjectMapper mapper, 
			final ITemplateCallback... callbacks) {
		final Map<Integer, Object> params = iparams.getParams();
		final Map<Integer, Class> paramclz = iparamclz.getParams();
		return getObject(stmn, params, paramclz, outIdx, outtype, mapper, callbacks);
	}

	public Map getMap(final String stmn, final Map<Integer, Object> params, final Map<Integer, Class> paramClz, final int outIdx, final IResultsetMapper mapper) {
		Map out = (Map) jdbcTemplate.execute(
			     new CallableStatementCreator() {
			         public CallableStatement createCallableStatement(Connection con) throws SQLException {
			            CallableStatement cs = con.prepareCall(stmn);
			            cs.clearParameters();
			            setParams(cs, params, paramClz);
			            if (outIdx>0) cs.registerOutParameter(outIdx, OracleTypes.CURSOR);//
			            return cs;
			         }
			      }, new CallableStatementCallback() {
			         public Object doInCallableStatement(CallableStatement cs) throws SQLException,DataAccessException {
			            cs.execute();
			            if (outIdx<1) return new Object[0];
			            ResultSet rs = (ResultSet) cs.getObject(outIdx);
			        	Map resultsList = new HashMap();
			        	int idx = 0;
			            while (rs.next()) {
			              mapper.map(rs, resultsList);
			            }
			            rs.close();
			            return resultsList;
			         }
			   });

		return out;
	}

	public Map getMap(final String stmn, IParamsMap iparams, IParamClzMap iparamclz, final int outIdx, final IResultsetMapper mapper) {
		final Map<Integer, Object> params = iparams.getParams();
		final Map<Integer, Class> paramclz = iparamclz.getParams();
		return getMap(stmn, params, paramclz, outIdx, mapper);
	}
	
	public static interface IParamsMap { public Map<Integer, Object> getParams(); }

	public static interface IParamClzMap { public Map<Integer, Class> getParams(); }

	public static interface IOutFields { public List<String> getOutFields(); }

	public static interface IColumnHeaders { public List<String> getColumnHeaders(); }

	public static interface IObjectMapper { public Object convert(ResultSet rs); }
	
	public static interface IResultsetMapper { public void map(ResultSet rs, Map out); }

	public static class PropConfig {
		String prop;
		Class propClass;

		public PropConfig(String prop, Class propClass) {
			this.prop = prop;
			this.propClass = propClass;
		}

		public String getProp() {
			return prop;
		}

		public void setProp(String prop) {
			this.prop = prop;
		}

		public Class getPropClass() {
			return propClass;
		}

		public void setPropClass(Class propClass) {
			this.propClass = propClass;
		}
	}

	public static void setPropWithRS(Object o, ResultSet rs, Map<String, PropConfig> outFieldsToProps) {
		if (o==null || rs==null || outFieldsToProps==null) return;
		for (String field : outFieldsToProps.keySet()) {
			PropConfig prop = outFieldsToProps.get(field);
			Object val = null;
			try {
				val = rs.getObject(field);
			} catch(Exception e) {
				e.printStackTrace();
				continue;
			}
			Method m = null;
			try {
				m = ObjectPropertiesUtil.setterMethod(o.getClass(), prop.getProp(), prop.getPropClass());
			} catch(Exception e) {
				e.printStackTrace();
				continue;
			}
			try {
				m.invoke(o, val);
			} catch(Exception e) {
				e.printStackTrace();
				continue;
			}
		}
	}
	
	public enum TemplateCallbackType {
		TypeCreate,
		TypeExecute
	}
	
	public static interface ITemplateCallback {
		public TemplateCallbackType getType();
		public void exec(CallableStatement cs) throws SQLException;
	}
	
	static Pattern PATTERN_ORAERR = Pattern.compile("^ORA-\\d{5}:", Pattern.CASE_INSENSITIVE);

	public Object queryForObject(String sql, Object[] args, RowMapper mapper) {
		return jdbcTemplate.queryForObject(sql, args, mapper);
	}
	
	public static void main(String[] args) {
		Matcher match = Pattern.compile("^ORA-\\d{5}:", Pattern.CASE_INSENSITIVE).matcher("ORA-12899: value too large for column \"MPD\".\"LAC_PROVIDER\".\"YEAR_GRADUATED\" (actual: 8, maximum: 4)\nORA-01403: no data found");
		System.out.println(match.find());
	}
}
