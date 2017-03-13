package com.stibosystems.restdemo;

import com.sun.org.apache.xpath.internal.SourceTree;

import javax.sound.midi.Soundbank;

public class Main {

    public static void main(String[] args) throws Exception {
        String systemName = args[0];
        String userName = args[1];
        String password = args[2];
        String iepID = args[3];
        RESTClient restClient = new RESTClient(systemName, userName, password);
        for(BGPImportStatus bgpImportStatus : restClient.getIEPBackgroundProcessIDs(iepID)) {
            System.out.println("(" + bgpImportStatus.type + ") - Status on import of file " + restClient.getImportFileNameFromBGPExecutionReport(bgpImportStatus.bgpID) + ", status " + bgpImportStatus.status + ", progress " + bgpImportStatus.progess + "%, (BGP ID: " + bgpImportStatus.bgpID + ")") ;
        }
    }
}
