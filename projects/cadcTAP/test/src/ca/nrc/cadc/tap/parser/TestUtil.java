/*
************************************************************************
*******************  CANADIAN ASTRONOMY DATA CENTRE  *******************
**************  CENTRE CANADIEN DE DONNÉES ASTRONOMIQUES  **************
*
*  (c) 2009.                            (c) 2009.
*  Government of Canada                 Gouvernement du Canada
*  National Research Council            Conseil national de recherches
*  Ottawa, Canada, K1A 0R6              Ottawa, Canada, K1A 0R6
*  All rights reserved                  Tous droits réservés
*                                       
*  NRC disclaims any warranties,        Le CNRC dénie toute garantie
*  expressed, implied, or               énoncée, implicite ou légale,
*  statutory, of any kind with          de quelque nature que ce
*  respect to the software,             soit, concernant le logiciel,
*  including without limitation         y compris sans restriction
*  any warranty of merchantability      toute garantie de valeur
*  or fitness for a particular          marchande ou de pertinence
*  purpose. NRC shall not be            pour un usage particulier.
*  liable in any event for any          Le CNRC ne pourra en aucun cas
*  damages, whether direct or           être tenu responsable de tout
*  indirect, special or general,        dommage, direct ou indirect,
*  consequential or incidental,         particulier ou général,
*  arising from the use of the          accessoire ou fortuit, résultant
*  software.  Neither the name          de l'utilisation du logiciel. Ni
*  of the National Research             le nom du Conseil National de
*  Council of Canada nor the            Recherches du Canada ni les noms
*  names of its contributors may        de ses  participants ne peuvent
*  be used to endorse or promote        être utilisés pour approuver ou
*  products derived from this           promouvoir les produits dérivés
*  software without specific prior      de ce logiciel sans autorisation
*  written permission.                  préalable et particulière
*                                       par écrit.
*                                       
*  This file is part of the             Ce fichier fait partie du projet
*  OpenCADC project.                    OpenCADC.
*                                       
*  OpenCADC is free software:           OpenCADC est un logiciel libre ;
*  you can redistribute it and/or       vous pouvez le redistribuer ou le
*  modify it under the terms of         modifier suivant les termes de
*  the GNU Affero General Public        la “GNU Affero General Public
*  License as published by the          License” telle que publiée
*  Free Software Foundation,            par la Free Software Foundation
*  either version 3 of the              : soit la version 3 de cette
*  License, or (at your option)         licence, soit (à votre gré)
*  any later version.                   toute version ultérieure.
*                                       
*  OpenCADC is distributed in the       OpenCADC est distribué
*  hope that it will be useful,         dans l’espoir qu’il vous
*  but WITHOUT ANY WARRANTY;            sera utile, mais SANS AUCUNE
*  without even the implied             GARANTIE : sans même la garantie
*  warranty of MERCHANTABILITY          implicite de COMMERCIALISABILITÉ
*  or FITNESS FOR A PARTICULAR          ni d’ADÉQUATION À UN OBJECTIF
*  PURPOSE.  See the GNU Affero         PARTICULIER. Consultez la Licence
*  General Public License for           Générale Publique GNU Affero
*  more details.                        pour plus de détails.
*                                       
*  You should have received             Vous devriez avoir reçu une
*  a copy of the GNU Affero             copie de la Licence Générale
*  General Public License along         Publique GNU Affero avec
*  with OpenCADC.  If not, see          OpenCADC ; si ce n’est
*  <http://www.gnu.org/licenses/>.      pas le cas, consultez :
*                                       <http://www.gnu.org/licenses/>.
*
*  $Revision: 4 $
*
************************************************************************
*/

/**
 * 
 */
package ca.nrc.cadc.tap.parser;

import java.util.ArrayList;
import java.util.List;

import ca.nrc.cadc.tap.schema.ColumnDesc;
import ca.nrc.cadc.tap.schema.KeyDesc;
import ca.nrc.cadc.tap.schema.SchemaDesc;
import ca.nrc.cadc.tap.schema.TableDesc;
import ca.nrc.cadc.tap.schema.TapSchema;

/**
 * Utility class solely for the purpose of testing.
 * 
 * @author Sailor Zhang
 *
 */
public class TestUtil
{
    /*
    private static String PROPERTY_FILE = "postgresql_sql.properties";
    public static TapProperties getPropertiesInstance() throws Exception
    {
        TapProperties prop;
        try
        {
            prop = new TapProperties(PROPERTY_FILE);
        } catch (Exception e)
        {
            throw e;
        }
        return prop;
    }
    */
    
    /**
     * load a TAP Schema for test purpose.
     * 
     */
    public static TapSchema loadDefaultTapSchema()
    {
        return mockTapSchema();
    }

    /**
     * @return a mocked TAP schema
     */
    public static TapSchema mockTapSchema()
    {
        String schemaName = "tap_schema";
        SchemaDesc sd = new SchemaDesc(schemaName, "description", "utype");
        List<SchemaDesc> sdList = new ArrayList<SchemaDesc>();
        sdList.add(sd);

        List<TableDesc> tdList;
        tdList = new ArrayList<TableDesc>();
        sd.setTableDescs(tdList);
        
        ColumnDesc cd;
        String tn; // table name
        List<ColumnDesc> cdList;
        TableDesc td;

        // custom test table in tap_schema
        tn = "alldatatypes";
        td = new TableDesc(schemaName, tn, "description", "utype");
        tdList.add(td);
        cdList = new ArrayList<ColumnDesc>();
        td.setColumnDescs(cdList);
        cdList.add( new ColumnDesc(tn, "t_integer", "int column", null, null, null, "adql:INTEGER", null) );
        cdList.add( new ColumnDesc(tn, "t_long", "long column", null, null, null, "adql:BIGINT", null) );
        cdList.add( new ColumnDesc(tn, "t_float", "float column", null, null, null, "adql:REAL", null) );
        cdList.add( new ColumnDesc(tn, "t_double", "double column", null, null, null, "adql:DOUBLE", null) );
        cdList.add( new ColumnDesc(tn, "t_char", "char column", null, null, null, "adql:CHAR", 8) );
        cdList.add( new ColumnDesc(tn, "t_varchar", "varchar column", null, null, null, "adql:VARCHAR", 8) );
        cdList.add( new ColumnDesc(tn, "t_string", "test column", null, null, null, "adql:VARCHAR", 8) );
        cdList.add( new ColumnDesc(tn, "t_bytes", "varbinary column", null, null, null, "adql:BLOB", null) );
        cdList.add( new ColumnDesc(tn, "t_text", "clob column", null, null, null, "adql:CLOB", null) );
        cdList.add( new ColumnDesc(tn, "t_point", "point column", null, null, null, "adql:POINT", null) );
        cdList.add( new ColumnDesc(tn, "t_region", "region column", null, null, null, "adql:REGION", null) );
        cdList.add( new ColumnDesc(tn, "t_timestamp", "timestamp column", null, null, null, "adql:TIMESTAMP", null) );
        cdList.add( new ColumnDesc(tn, "t_int_array", "int[] column", null, null, null, "votable:int", 2) );
        cdList.add( new ColumnDesc(tn, "t_double_array", "double[] column", null, null, null, "votable:double", 2) );
        cdList.add( new ColumnDesc(tn, "t_complete", "column with full metadata", "test:come.data.model","meta.ucd", "m", "votable:double", 2) );

        // standard minimal self-describing tap_schema tables
        tn = "tables";
        td = new TableDesc(schemaName, tn, "description", "utype");
        tdList.add(td);
        cdList = new ArrayList<ColumnDesc>();
        td.setColumnDescs(cdList);
        cd = new ColumnDesc(); cdList.add(cd); cd.setTableName(tn); cd.setColumnName("schema_name"); cd.setDatatype("adql:VARCHAR"); cd.setSize(16);
        cd = new ColumnDesc(); cdList.add(cd); cd.setTableName(tn); cd.setColumnName("table_name"); cd.setDatatype("adql:VARCHAR"); cd.setSize(16);
        cd = new ColumnDesc(); cdList.add(cd); cd.setTableName(tn); cd.setColumnName("utype"); cd.setDatatype("adql:VARCHAR"); cd.setSize(16);
        cd = new ColumnDesc(); cdList.add(cd); cd.setTableName(tn); cd.setColumnName("description"); cd.setDatatype("adql:VARCHAR"); cd.setSize(16);
        
        tn = "columns";
        td = new TableDesc(schemaName, tn, "description", "utype");
        tdList.add(td);
        cdList = new ArrayList<ColumnDesc>();
        td.setColumnDescs(cdList);
        cd = new ColumnDesc(); cdList.add(cd); cd.setTableName(tn); cd.setColumnName("table_name"); cd.setDatatype("adql:VARCHAR"); cd.setSize(16);
        cd = new ColumnDesc(); cdList.add(cd); cd.setTableName(tn); cd.setColumnName("column_name"); cd.setDatatype("adql:VARCHAR"); cd.setSize(16);
        cd = new ColumnDesc(); cdList.add(cd); cd.setTableName(tn); cd.setColumnName("utype"); cd.setDatatype("adql:VARCHAR"); cd.setSize(16);
        cd = new ColumnDesc(); cdList.add(cd); cd.setTableName(tn); cd.setColumnName("ucd"); cd.setDatatype("adql:VARCHAR"); cd.setSize(16);
        cd = new ColumnDesc(); cdList.add(cd); cd.setTableName(tn); cd.setColumnName("unit"); cd.setDatatype("adql:VARCHAR"); cd.setSize(16);
        cd = new ColumnDesc(); cdList.add(cd); cd.setTableName(tn); cd.setColumnName("description"); cd.setDatatype("adql:VARCHAR"); cd.setSize(16);
        cd = new ColumnDesc(); cdList.add(cd); cd.setTableName(tn); cd.setColumnName("datatype"); cd.setDatatype("adql:VARCHAR"); cd.setSize(16);
        cd = new ColumnDesc(); cdList.add(cd); cd.setTableName(tn); cd.setColumnName("size"); cd.setDatatype("adql:INTEGER");
        cd = new ColumnDesc(); cdList.add(cd); cd.setTableName(tn); cd.setColumnName("principal"); cd.setDatatype("adql:INTEGER");
        cd = new ColumnDesc(); cdList.add(cd); cd.setTableName(tn); cd.setColumnName("indexed"); cd.setDatatype("adql:INTEGER");
        cd = new ColumnDesc(); cdList.add(cd); cd.setTableName(tn); cd.setColumnName("std"); cd.setDatatype("adql:INTEGER");

        tn = "keys";
        td = new TableDesc(schemaName, tn, "description", "utype");
        tdList.add(td);
        cdList = new ArrayList<ColumnDesc>();
        td.setColumnDescs(cdList);
        cd = new ColumnDesc(); cdList.add(cd); cd.setTableName(tn); cd.setColumnName("key_id"); cd.setDatatype("adql:VARCHAR"); cd.setSize(16);
        cd = new ColumnDesc(); cdList.add(cd); cd.setTableName(tn); cd.setColumnName("from_table"); cd.setDatatype("adql:VARCHAR"); cd.setSize(16);
        cd = new ColumnDesc(); cdList.add(cd); cd.setTableName(tn); cd.setColumnName("target_table"); cd.setDatatype("adql:VARCHAR"); cd.setSize(16);
        cd = new ColumnDesc(); cdList.add(cd); cd.setTableName(tn); cd.setColumnName("utype"); cd.setDatatype("adql:VARCHAR"); cd.setSize(16);
        cd = new ColumnDesc(); cdList.add(cd); cd.setTableName(tn); cd.setColumnName("description"); cd.setDatatype("adql:VARCHAR"); cd.setSize(16);

        tn = "key_columns";
        td = new TableDesc(schemaName, tn, "description", "utype");
        tdList.add(td);
        cdList = new ArrayList<ColumnDesc>();
        td.setColumnDescs(cdList);
        cd = new ColumnDesc(); cdList.add(cd); cd.setTableName(tn); cd.setColumnName("key_id"); cd.setDatatype("adql:VARCHAR"); cd.setSize(16);
        cd = new ColumnDesc(); cdList.add(cd); cd.setTableName(tn); cd.setColumnName("from_column"); cd.setDatatype("adql:VARCHAR"); cd.setSize(16);
        cd = new ColumnDesc(); cdList.add(cd); cd.setTableName(tn); cd.setColumnName("target_column"); cd.setDatatype("adql:VARCHAR"); cd.setSize(16);

        List<KeyDesc> kdList;
        kdList = new ArrayList<KeyDesc>();
        // TODO: add the standard key descs
        TapSchema ts = new TapSchema(sdList, kdList);
        return ts;
    }

    /*
    public static TapSchema loadTapSchemaFromDb()
    {
        TapSchema rtn = null;
        try
        {
            TapProperties properties = TestUtil.getPropertiesInstance();

            String JDBC_DRIVER = properties.getProperty("JDBC_DRIVER");
            String JDBC_URL = properties.getProperty("JDBC_URL");
            String USERNAME = properties.getProperty("USERNAME");
            String PASSWORD = properties.getProperty("PASSWORD");
            boolean SUPPRESS_CLOSE = properties.getPropertyBooloean("SUPPRESS_CLOSE");

            try
            {
                Class.forName(JDBC_DRIVER);
            } catch (ClassNotFoundException e)
            {
                String msg = "JDBC Driver not found.";
                throw new MissingResourceException(msg, JDBC_DRIVER, null);
            }
            DataSource ds = new SingleConnectionDataSource(JDBC_URL, USERNAME, PASSWORD, SUPPRESS_CLOSE);
            TapSchemaDAO dao = new TapSchemaDAO(ds);
            rtn = dao.get();
        } catch (Exception ex)
        {
            throw new UnsupportedOperationException(ex);
        }
        return rtn;
    }
    */

    public static String getCallingMethod() {
        return trace(Thread.currentThread().getStackTrace(), 2);
    }
 
    public static String getCallingMethod(int level) {
        return trace(Thread.currentThread().getStackTrace(), 2 + level);
    }
 
    private static String trace(StackTraceElement e[], int level) {
        String rtn=null;
        if(e != null && e.length >= level) {
            StackTraceElement s = e[level];
            if(s != null) 
                rtn = s.getMethodName();
        }
        return rtn;
    }
}
