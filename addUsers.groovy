import com.collibra.dgc.core.api.dto.user.AddUserRequest

def fileUpload = 

def firstName = execution.getVariable("firstName")
def lastName = execution.getVariable("lastName")
def userName = "${firstName}${lastName}"
def email = execution.getVariable("email")
def dataAcademyId = [string2Uuid("01907e0c-f788-755f-95e4-84156ae02fe7")]

loggerApi.info("userName: ${userName}, firstName: ${firstName}, lastName: ${lastName}, emailAddress: ${emailAddress}, dataAcademyId: ${dataAcademyId}")

def addNewUser = userApi.addUser(AddUserRequest.builder()
      .userName(userName)                                 //string
      .firstName(firstName)                                //string
      .lastName(lastName)                                 //string
      .emailAddress(email)                             //string
      .userGroupIds(dataAcademyId)                //list<UUID>
      .build())

