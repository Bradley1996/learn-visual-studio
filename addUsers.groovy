import com.collibra.dgc.core.api.dto.user.AddUserRequest
import org.apache.poi.xssf.usermodel.XSSFWorkbook

def dataAcademyId = [string2Uuid("01907e0c-f788-755f-95e4-84156ae02fe7")]

if (fileUpload != null) {
// Get file stream from uploaded file
def fileStream = fileApi.getFileAsStream(string2Uuid(fileUpload))
def workbook = new XSSFWorkbook(fileStream)

def rowIter = sheet.iterator()
rowIter.next() // Skip header row

def addUserRequests = []

// Iterate through Excel sheet
while (rowIter.hasNext()) {
    def row = rowIter.next()

    def firstName = row.getCell(0)?.getStringCellValue()?.trim()
    def lastName = row.getCell(1)?.getStringCellValue()?.trim()
    def email = row.getCell(3)?.getStringCellValue()?.trim()
    def userName = "${firstName} ${lastName}"

    addUserRequests.add(AddUserRequest.builder()
        .userName(userName)
        .firstName(firstName)
        .lastName(lastName)
        .emailAddress(email)
        .groups(dataAcademyId) 
        .build())
}

// Add new users
userApi.addUsers(addUserRequests)

loggerApi.info("Users added: ${addUserRequests.size()}")

// Close resources
workbook.close()
fileStream.close()

} else {
def firstName = execution.getVariable("firstName")
def lastName = execution.getVariable("lastName")
def userName = "${firstName} ${lastName}"
def email = execution.getVariable("email")

loggerApi.info("userName: ${userName}, firstName: ${firstName}, lastName: ${lastName}, emailAddress: ${email}, dataAcademyId: ${dataAcademyId}")

def addNewUser = userApi.addUser(AddUserRequest.builder()
      .userName(userName)                              //string
      .firstName(firstName)                            //string
      .lastName(lastName)                              //string
      .emailAddress(email)                             //string
      .userGroupIds(dataAcademyId)                     //list<UUID>
      .build())
}



// import org.apache.poi.xssf.usermodel.XSSFWorkbook;
// import com.collibra.dgc.workflow.api.exception.WorkflowException;
// import com.collibra.dgc.core.api.dto.user.AddUserRequest;
// import com.collibra.dgc.core.api.dto.user.FindUsersRequest;
// import com.collibra.dgc.core.api.dto.user.SetUserGroupsForUserRequest;
// import com.collibra.dgc.core.api.model.user.LicenseType;
// import com.collibra.dgc.core.api.dto.usergroup.FindUserGroupsRequest;
// import com.collibra.dgc.core.api.dto.MatchMode;
// import com.collibra.dgc.core.api.dto.usergroup.AddUserGroupRequest;
// import com.collibra.dgc.core.api.dto.usergroup.AddUserGroupsRequest;
// import com.collibra.dgc.core.api.dto.user.ChangeUserRequest;
// import com.collibra.dgc.core.api.dto.user.RemoveUserFromUserGroupsRequest;

// //userGroupMap = [:]
// groupList = []
// loggerApi.info("**** importFile: ${importFile}")

// if(!fileApi.getFileInfo(string2Uuid(importFile.toString())).getName().endsWith('.xlsx')){
// 	def dgcError = new WorkflowException("File Type Not Recognized, please use .xlsx")
// 	dgcError.setTitleMessage("File Type Not Recognized");
// 	throw dgcError;  
// }

// workbook = new XSSFWorkbook(fileApi.getFileAsStream(string2Uuid(importFile)))
// def sheet = workbook.getSheetAt(0)
// if(sheet.getPhysicalNumberOfRows() > 0) {
// 	def rowIter = sheet.iterator()
// 	def userFirstName = ""
// 	def userLastName = ""
// 	def userName = ""
// 	def userEmailAddress = ""
// 	def userLicenseType = ""

// 	def row = rowIter.next() // skip the header

// 	userMapList = []
// 	while (rowIter.hasNext())
// 	{
// 		row = rowIter.next()
// 		userMap = [:]
// 		if(row.getCell(0) != null){
// 			userMap["firstName"] = row.getCell(0).getStringCellValue().trim()
// 		}
// 		if(row.getCell(1) != null){
// 			userMap["lastName"] = row.getCell(1).getStringCellValue().trim()
// 		}
// 		if(row.getCell(2) != null){
// 			userMap["userName"] = row.getCell(2).getStringCellValue().trim()
// 		}
// 		if(row.getCell(3) != null){
// 			userMap["emailAddress"] = row.getCell(3).getStringCellValue().trim()
// 		}
// 		if(row.getCell(4) != null){
// 			userLicenseType = row.getCell(4).getStringCellValue().trim()
// 			if(userLicenseType.toUpperCase() != "AUTHOR"){
// 				userMap["userLicenseType"] = "CONSUMER"
// 			}
// 			else{
// 				userMap["userLicenseType"] = "AUTHOR"
// 			}
// 		}
// 		else{
// 			userMap["userLicenseType"] = "CONSUMER"
// 		}
// 		if(row.getCell(5) != null){
// 			//get the groups
// 			groupNames = row.getCell(5).getStringCellValue().trim()
// 			userGroups = utility.toList(groupNames)
// 			userMap["userGroups"] = userGroups.collect{it.substring(1, it.length()-1).trim()}
// 			//add group to user map
// 			//userGroupMap["${row.getCell(2).getStringCellValue().trim()}"] = userGroups.collect{it.substring(1, it.length()-1).trim()}
// 			//loggerApi.info("userGroupMap.userName: "+ userGroupMap)
// 			groupList.addAll(userGroups.collect{it.substring(1, it.length()-1).trim()})
// 		}
// 		userMapList.add(userMap)
// 	}
// }

// def addRequestList = []
// def changeRequestList = []
// userCount = userApi.findUsers(FindUsersRequest.builder().limit(1).includeDisabled(true).build()).getTotal()
// allUsers = userApi.findUsers(FindUsersRequest.builder().limit(userCount.intValue()).includeDisabled(true).build()).getResults()

// for(userMap in userMapList){
// 	if(userMap.userName){
// 		existingUser = allUsers.findAll{it.getUserName()==userMap.userName}
// 		//loggerApi.info("username: ${userMap.userName} || existing: ${existingUser}")	
// 		if(existingUser.isEmpty()){ //if user does not exist
// 			addRequestList.add(AddUserRequest.builder()
// 				.userName(userMap.userName)
// 				.firstName(userMap.firstName)
// 				.lastName(userMap.lastName)
// 				.emailAddress(userMap.emailAddress)
// 				.licenseType(LicenseType.valueOf(userMap.userLicenseType))
// 				.build())
// 		}
// 		else{ //if user currently exists
// 			changeRequestList.add(ChangeUserRequest.builder()
// 				.id(existingUser.first().getId())
// 				.username(userMap.userName)
// 				.firstName(userMap.firstName)
// 				.lastName(userMap.lastName)
// 				.email(userMap.emailAddress)
// 				.licenseType(LicenseType.valueOf(userMap.userLicenseType))
// 				.build())
// 		}
// 	}
// }
// loggerApi.info("addRequestList size: ${addRequestList.size()}")
// loggerApi.info("changeRequestList size: ${changeRequestList.size()}")
// userApi.addUsers(addRequestList)
// for(changeRequest in changeRequestList){
// 	userApi.changeUser(changeRequest)
// }

// if(!userGroupsSetting.contains("ignore")){
// 	userGroupsToCreate = []
// 	for(group in groupList.unique()){
// 		groupsFound = userGroupApi.findUserGroups(FindUserGroupsRequest.builder()
// 						.name(group)
// 						.nameMatchMode(MatchMode.valueOf("EXACT"))
// 						.build()).getResults()
// 		if(!groupsFound.size())  //if group found then add found group
// 		{
// 			userGroupsToCreate.add(group)
// 		}
// 	}

// 	loggerApi.info("userGroupsToCreate: ${userGroupsToCreate}")
// 	if(userGroupsToCreate.size()){
// 		//create groups
// 		userGroupApi.addUserGroups(AddUserGroupsRequest.builder().names(userGroupsToCreate.unique()).build())
// 	}

// 	//set user groups
// 	for(userMap in userMapList){
// 		if(userMap.userName){
// 			userId = userApi.findUsers(FindUsersRequest.builder().name(userMap.userName).includeDisabled(true).build()).getResults().findAll{it.getUserName()==userMap.userName}.first().getId()
// 			if(userMap.userGroups?.isEmpty()){
// 				groupIdList = userGroupApi.findUserGroups(FindUserGroupsRequest.builder().userId(userId).includeEveryone(false).build()).getResults().findAll{it.getId().toString()!="00000000-0000-0000-0000-000001000002"}.collect{it.getId()}
// 				//loggerApi.info("groupIdList: ${groupIdList}")
// 				if(!groupIdList.isEmpty()){
// 					userApi.removeUserFromUserGroups(RemoveUserFromUserGroupsRequest.builder().userId(userId).userGroupIds(groupIdList).build())
// 				}
// 			}
// 			else{
// 				userGroupIds = []
// 				for(grpName in userMap.userGroups){
// 					//loggerApi.info("grpname: ${grpName}")
// 					userGroupId = userGroupApi.findUserGroups(FindUserGroupsRequest.builder()
// 									.name(grpName)
// 									.nameMatchMode(MatchMode.valueOf("EXACT"))
// 									.build()).getResults().first().getId()
// 					userGroupIds.add(userGroupId)
// 				}
// 				loggerApi.info("username: ${userMap.userName} || userGroupIds: ${userGroupIds}")
// 				userApi.setGroupsForUser(SetUserGroupsForUserRequest.builder().userId(userId).userGroupIds(userGroupIds).build())
// 			}
// 		}
// 	}
// }