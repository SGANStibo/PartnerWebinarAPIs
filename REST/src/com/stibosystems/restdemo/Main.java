package com.stibosystems.restdemo;

import com.sun.org.apache.xpath.internal.SourceTree;

import javax.sound.midi.Soundbank;

public class Main {

    public static void main(String[] args) throws Exception {
        RESTClient restClient = new RESTClient(args[0], args[1], args[2]);
        for(BGPImportStatus bgpImportStatus : restClient.getIEPBackgroundProcessIDs("AssetAutoImport")) {
            System.out.println("(" + bgpImportStatus.type + ") - Status on import of file " + restClient.getImportFileNameFromBGPExecutionReport(bgpImportStatus.bgpID) + ", status " + bgpImportStatus.status + ", progress " + bgpImportStatus.progess + "%, (BGP ID: " + bgpImportStatus.bgpID + ")") ;
        }
    }
}
