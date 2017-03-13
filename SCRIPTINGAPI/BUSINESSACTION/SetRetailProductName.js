var isRetailProductValue = node.getValue("IsRetailProduct").getSimpleValue();

if(isRetailProductValue != null && isRetailProductValue.equals("Yes")) {
	var retailProductNameValue = node.getValue("RetailProductName").getSimpleValue();
	if(retailProductNameValue == null || retailProductNameValue.equals("")) {
		node.getValue("RetailProductName").setSimpleValue(node.getName());
	}
}

