package com.stibosystems.restdemo;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by sgan on 3/10/17.
 */
public class RESTClient {
    private String baseURL;
    private String username;
    private String password;

    public RESTClient(String stepSystemURL, String username, String password) {
        baseURL = "http://" + stepSystemURL + "/restapi/";
        this.username = username;
        this.password = password;
    }

    public List<BGPImportStatus> getIEPBackgroundProcessIDs(String IEPID) throws Exception {
        String resourceURL = baseURL + "integrationendpoints/" + IEPID + "/backgroundprocesses";

        HttpURLConnection conn = getHttpURLConnection(resourceURL);

        /**
         * XML Should look something like this:
         <BackgroundProcess>
         <Active>
         <Process Created="2016-02-19 12:48:49 CET" Description="Import started for endpoint 'Apparel Hotfolder' (2016-02-19 12:48:49)" Finished="2016-02-19 12:48:54 CET" NumberOfErrors="2" NumberOfWarnings="1" Progress="0" StartedBy="IARE" Started="2016-02-19 12:48:54 CET" Status="failed" ID="BGP_198003"/>
         <Process Created="2016-02-19 13:00:47 CET" Description="Import started for endpoint 'Apparel Hotfolder' (2016-02-19 13:00:47)" Finished="2016-02-19 13:00:52 CET" NumberOfErrors="2" NumberOfWarnings="1" Progress="0" StartedBy="IARE" Started="2016-02-19 13:00:52 CET" Status="failed" ID="BGP_198009"/>
         </Active>
         </BackgroundProcess>
         */

        Document responseDoc = parseXML(conn.getInputStream());

        //There can be only one, as all active BGPs are inside the same tag
        NodeList activeTagNodeList = responseDoc.getElementsByTagName("Active");

        if(activeTagNodeList.getLength() <= 0) {
            return new LinkedList<>();
        }

        //Get the Active-node
        Node activeTag = activeTagNodeList.item(0);

        List<BGPImportStatus> bgpIDs = new LinkedList<>();
        //Find the IDs
        for (int i = 0; i < activeTag.getChildNodes().getLength(); i++) {
            Node bgpNode = activeTag.getChildNodes().item(i);
            BGPImportStatus fileImportStatus = new BGPImportStatus();
            fileImportStatus.bgpID = bgpNode.getAttributes().getNamedItem("ID").getTextContent();
            fileImportStatus.progess = bgpNode.getAttributes().getNamedItem("Progress").getTextContent();
            fileImportStatus.status = bgpNode.getAttributes().getNamedItem("Status").getTextContent();
            bgpIDs.add(fileImportStatus);
        }

        //Drop the connection
        conn.disconnect();

        return bgpIDs;
    }

    public String getImportFileNameFromBGPExecutionReport(String bgpID) throws Exception {
        String resourceURL = baseURL + "bgpinstance/" + bgpID + "/executionreport";

        HttpURLConnection conn = getHttpURLConnection(resourceURL);

        /**
         * XML Should look something like this:
         <ExecutionReport ID="BGP_198003">
         <Row Kind="Info">
         Processing the file)(s) 'M:\hotfolders\ApparelImport\IN\output1.xml' (Fri Feb 19 12:48:54 CET 2016)
         </Row>
         <Row Kind="Info">Conversion started (Fri Feb 19 12:48:54 CET 2016)</Row>
         <Row Kind="Warning">
         Cannot guess excel version of import-source (will try using Excel 2003 format): Your InputStream was neither an OLE2 stream, nor an OOXML stream
         </Row>
         <Row Kind="Error">
         com.stibo.importmanager.ConversionError: java.io.IOException: Invalid header signature; read 0x6576206C6D783F3C, expected 0xE11AB1A1E011CFD0<br>java.io.IOException: Invalid header signature; read 0x6576206C6D783F3C, expected 0xE11AB1A1E011CFD0<br>Invalid header signature; read 0x6576206C6D783F3C, expected 0xE11AB1A1E011CFD0
         </Row>
         <Row Kind="Error">
         Caught RuntimeException at Fri Feb 19 12:48:54 CET 2016: com.stibo.importmanager.ConversionError: java.io.IOException: Invalid header signature; read 0x6576206C6D783F3C, expected 0xE11AB1A1E011CFD0
         </Row>
         </ExecutionReport>
         */

        Document responseDoc = parseXML(conn.getInputStream());

        //There can be only one, as all active BGPs are inside the same tag
        NodeList rowTagNodeList = responseDoc.getElementsByTagName("Row");

        if(rowTagNodeList.getLength() <= 0) {
            throw new RuntimeException("Error - looking at import BGP without any rows.");
        }

        //the first row-tag should contain the flename
        Node firstRowTag = rowTagNodeList.item(0);
        String firstRowTagContent = firstRowTag.getTextContent();

        int firstIndexOfPling = firstRowTagContent.indexOf("'");
        int lastIndexOfPling = firstRowTagContent.lastIndexOf("'");
        String fullPathOfFile = firstRowTagContent.substring(firstIndexOfPling+1, lastIndexOfPling);

        //Drop the connection
        conn.disconnect();

        return fullPathOfFile;
    }

    private HttpURLConnection getHttpURLConnection(String resourceURL) throws IOException {
        URL url = new URL(resourceURL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setRequestMethod("GET");

        //Set up basic auth
        String encoded = Base64.getEncoder().encodeToString((username+":"+password).getBytes(StandardCharsets.UTF_8));  //Java 8
        conn.setRequestProperty("Authorization", "Basic "+encoded);

        //Ececute the connection
        if (conn.getResponseCode() != 200) {
            throw new RuntimeException("Failed : HTTP error code : "
                    + conn.getResponseCode());
        }
        return conn;
    }

    private Document parseXML(InputStream stream)
            throws Exception
    {
        DocumentBuilderFactory objDocumentBuilderFactory = null;
        DocumentBuilder objDocumentBuilder = null;
        Document doc = null;
        objDocumentBuilderFactory = DocumentBuilderFactory.newInstance();
        objDocumentBuilder = objDocumentBuilderFactory.newDocumentBuilder();

        doc = objDocumentBuilder.parse(stream);

        return doc;
    }

}
