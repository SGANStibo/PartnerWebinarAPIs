package com.stibosystems.restdemo;

import com.sun.org.apache.xpath.internal.SourceTree;

import javax.sound.midi.Soundbank;

public class Main {

    public static void main(String[] args) throws Exception {
        RESTClient restClient = new RESTClient("enter system name here", "enter username here", "enter password here");
        for(BGPImportStatus bgpImportStatus : restClient.getIEPBackgroundProcessIDs("Apparel%20Hotfolder")) {
            System.out.println("Status on import of file " + restClient.getImportFileNameFromBGPExecutionReport(bgpImportStatus.bgpID) + ", status " + bgpImportStatus.status + ", progress " + bgpImportStatus.progess + "%, (BGP ID: " + bgpImportStatus.bgpID + ")") ;
        }
    }
}
