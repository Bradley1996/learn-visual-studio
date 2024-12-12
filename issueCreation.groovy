import com.collibra.dgc.core.api.dto.instance.issue.AddIssueRequest
import com.collibra.dgc.core.api.dto.instance.issue.RelatedAssetReference
import com.collibra.dgc.core.api.dto.user.FindUsersRequest
import com.collibra.dgc.core.api.model.ResourceType
import com.collibra.dgc.core.api.dto.instance.attachment.AddAttachmentRequest

def requesterId = userApi.getUserByUsername(initiator).getId()
def descriptionString = execution.getVariable("description")?.toString() ?: ""
def communityId = responsibleCommunity
def relatedAssets = execution.getVariable("relatedAssets") ?: []
def title = execution.getVariable("title")?.toString() ?: ""
def priority = execution.getVariable("priority")?.toString() ?: ""

loggerApi.info("Requester ID: ${requesterId}, descriptionString: ${descriptionString}, communityId: ${communityId}, relatedAssets: ${relatedAssets}, title: ${title}, priority: ${priority}")

//AddIssueRequest.relatedAssets requires a list. Convert relatedAssets into a list. If already a list, flatten() prevents a nested list from being created.
relatedAssets = [relatedAssets].flatten()

def relatedAssetsList = []
for (relatedAssetId in relatedAssets) {
    def relatedAssetRef = RelatedAssetReference.builder()
            .assetId(string2Uuid(relatedAssetId))
            .direction(true)
            .relationTypeId(string2Uuid("00000000-0000-0000-0000-000000007025"))  //resourceId for impacts asset
            .build()
    relatedAssetsList.add(relatedAssetRef)
}

loggerApi.info("relatedAssetsList: ${relatedAssetsList}",)

//
def newIssueUuid = issueApi.addIssue(AddIssueRequest.builder()
        .name(title)
        .description(descriptionString)
        .priority(priority)
        .responsibleCommunityId(string2Uuid(communityId))
        .relatedAssets(relatedAssetsList)
        .typeId(string2Uuid("00000000-0000-0000-0000-000000031001")) //UUID for Data Issue in Valcon Collibra Platform
        .requesterId(requesterId) // Assigns the 'requester' responsibility to the requester
        .build())
        .getId()

loggerApi.info("newIssueUuid: ${newIssueUuid}")

// Attach the uploaded document to the data issue created
if (binding.hasVariable("fileUpload")) {

    def inputFileName = fileApi.getFileInfo(string2Uuid(fileUpload)).getName()

    attachmentApi.addAttachment(AddAttachmentRequest.builder()
            .baseResourceId(newIssueUuid)
            .baseResourceType(ResourceType.valueOf("Asset"))
            .fileName(inputFileName)
            .fileStream(fileApi.getFileAsStream(string2Uuid(fileUpload)))
            .build()
    )
}

execution.setVariable("newIssueUuid", newIssueUuid)

