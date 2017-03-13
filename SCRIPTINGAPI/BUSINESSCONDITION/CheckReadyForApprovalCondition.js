var isRetailProductValue = node.getValue("IsRetailProduct").getSimpleValue();

logger.info(isRetailProductValue);

//isRetailProductValue now contains either "Yes", "No" or null
if(isRetailProductValue == null) {
	//Disallow approval - you have not set a critical value!
	var errMsg = new ValidationMessage();
	errMsg.ID = node.getID();
	return errMsg;
}

//Allow approval - value is set, to yes or no
return true;

