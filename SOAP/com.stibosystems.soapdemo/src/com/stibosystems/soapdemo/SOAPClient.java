package com.stibosystems.soapdemo;

import com.stibosystems.soapclientclasses.*;

import java.math.BigInteger;

/**
 * The SOAP client class, providing convenience methods for various checks / creates / searches
 */
public class SOAPClient {
    private String username;
    private String password;
    private StepPortType stepPort;

    public SOAPClient(String username, String password) {
        this.username = username;
        this.password = password;
        stepPort = new StepService().getStepPort();
    }

    public CreateReferenceResponseType setPrimaryImageReference(TreeNode product, TreeNode asset) throws CreateReferenceFault {
        CreateReferenceRequestType createReferenceRequest = new CreateReferenceRequestType();
        createReferenceRequest.setAccessContext(getAccessContext());
        createReferenceRequest.setOwnerURL(product.getUrl());
        createReferenceRequest.setTargetURL(asset.getUrl());
        createReferenceRequest.setReferenceType("Primary Image");
        return stepPort.createReference(createReferenceRequest);
    }


    public boolean hasPrimaryImageReference(TreeNode product) throws GetReferencesFault {
        GetReferencesRequestType getReferencesRequestType = new GetReferencesRequestType();
        getReferencesRequestType.setAccessContext(getAccessContext());
        getReferencesRequestType.setNodeURL(product.getUrl());
        GetReferencesResponseType getReferencesResponseType = stepPort.getReferences(getReferencesRequestType);
        for(Reference ref : getReferencesResponseType.getReference()) {
            if(ref.getReferenceType().equals("Primary Image")) {
                return true;
            }
        }
        return false;
    }

    public TreeNode findProductByAttributeValue(String attributeID, String attributeValue) throws QueryByAttributeFault {
        //Build the query - remember to set all the values!
        QueryByAttributeRequestType queryByAttributeRequestType = new QueryByAttributeRequestType();
        queryByAttributeRequestType.setAccessContext(getAccessContext());
        queryByAttributeRequestType.setAttributeURL("step://attribute?id=" + attributeID);
        queryByAttributeRequestType.setAttributeValue(attributeValue);
        queryByAttributeRequestType.setCaseSensitive(false);
        queryByAttributeRequestType.setMaxCount(1);
        queryByAttributeRequestType.setOffset(0);

        QueryByAttributeResponseType queryByAttributeResponseType = stepPort.queryByAttribute(queryByAttributeRequestType);
        if(queryByAttributeResponseType.getNode().size() > 0) {
            return queryByAttributeResponseType.getNode().get(0);
        }
        return null;
    }

    public TreeNode findAssetByName(String name) throws QueryByNameFault {
        QueryByNameRequestType queryByNameRequest = new QueryByNameRequestType();
        queryByNameRequest.setAccessContext(getAccessContext());
        queryByNameRequest.setCaseSensitive(false);
        queryByNameRequest.setMaxCount(BigInteger.ONE);
        queryByNameRequest.setOffset(BigInteger.ZERO);
        queryByNameRequest.setSort(false);
        queryByNameRequest.setQueryString(name);
        QueryByNameResponseType responseType = stepPort.queryByName(queryByNameRequest);
        if(responseType.getNode().size() > 0) {
            return responseType.getNode().get(0);
        }
        return null;
    }

    public AccessContext getAccessContext() {
        AccessContext accessContext = new AccessContext();
        accessContext.setUserName(username);
        accessContext.setPassword(password);
        //Running in GL context, Main workspace - normally we would not run in GL context, but a more specific one
        //But for demo purposes, we'll go with the simple choice
        accessContext.setContextUrl("step://context?id=GL");
        accessContext.setWorkspaceUrl("step://workspace?id=Main");
        accessContext.setPasswordType("password");
        return accessContext;
    }

}
