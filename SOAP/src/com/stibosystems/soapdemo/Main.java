package com.stibosystems.soapdemo;

import com.stibosystems.soapclientclasses.*;

import java.math.BigInteger;

/**
 * Demo for Webinar:
 * Find product by attribute serch
 * Find asset by name
 * Create Primary Image reference between them, if one does not already exist
 */
public class Main {

    public static void main(String[] args) throws GetTopProductFault, QueryByAttributeFault, GetReferencesFault, CreateReferenceFault, QueryByNameFault {
        String userName = args[0];
        String password = args[1];
        //Enter username / password here!
        SOAPClient soapClient = new SOAPClient(userName, password);

        String productAttribute = "ProductName";
        String productAttributeValue = "12069999";

        TreeNode product = soapClient.findProductByAttributeValue(productAttribute, productAttributeValue);
        if(product != null) {
            System.out.println("Found product " + product.getId());

            if (!soapClient.hasPrimaryImageReference(product)) {
                System.out.println("Product '" + product.getId() + "', did not already have a primary image reference.");

                String assetName = "007020611694";
                TreeNode asset = soapClient.findAssetByName(assetName);
                if(asset != null) {
                    System.out.println("Found asset " + asset.getId());
                    soapClient.setPrimaryImageReference(product, asset);
                    System.out.println("Reference between " + product.getId() + " and " + asset.getId() + " of type Primary Image has been created.");
                } else {
                    System.out.println("Found no asset with name " + assetName);
                }
            } else {
                System.out.println("Product already has primary image reference");
            }
        } else {
            System.out.println("Found no products matching search for value '" + productAttributeValue + "', for attribute '" + productAttribute + "'");
        }
    }




}
