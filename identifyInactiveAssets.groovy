import com.collibra.dgc.core.api.dto.instance.asset.FindAssetsRequest
import com.collibra.dgc.core.api.dto.instance.attribute.FindAttributesRequest
import groovy.time.TimeCategory

// Find all assets in the Toyota Community
def listAllAssets = assetApi.findAssets(FindAssetsRequest.builder()
        .communityId([string2Uuid("7ec1ec86-b573-48f7-9025-936c36592ca3")]) // List of communityIds to be filtered
        .build()
).getResults()

loggerApi.info("All assets found are : ${listAllAssets}")

/*Extract asset IDs from the results. .collect transforms each element (in this case transform asset
objects into assetIds) it refers to current object in list. id extracts the id of the current object.*/
def allAssetIds = listAllAssets.collect{it.id}

// Fetch the "Last Modified On" attribute for the assets
def allAttributes = []
for (assetId in allAssetIds) {
    def lastModifiedAttribute = attributeApi.findAttributes(FindAttributesRequest.builder()
            .assetId(assetId) // List of allAssetIds to be filtered
            .name("Last Modified On") // Target attribute name
            .build()
    ).getResults()
    allAttributes.add(lastModifiedAttribute)
}

loggerApi.info("Attributes received: ${allAttributes}")

// Filter attributes by specific value or condition
//Define the date range of when assets become inactive
use(TimeCategory) {
    def today = new Date()
    def sixMonthsAgo = today - 6.months

    def filteredAttributes = allAttributes.findAll { attribute ->
        def attributeDate = Date.parse("MM/dd/yyyy, h:mm a", attribute.value) // Parse the date string
        attributeDate < sixMonthsAgo // Compare to sixMonthsAgo date
    }

    loggerApi.info("Filtered attributes: ${filteredAttributes}")
}
//Retrieve the asset ids from the remaining attributes
def inactiveAssetIds = filteredAttributes.collect{it.assetId}


//Fetch asset objects and all details (name and community) for each inactiveAssetId
def inactiveAssets = assetApi.findAssets(FindAssetsRequest.builder()
        .assetIds(inactiveAssetIds) // Provide a list of inactive asset Ids
        .build()
).getResults()

//Initialize a list to store the details of the inactive assets
def listAssetDetails = []

//Extract the inactive asset details and store them in listAssetDetails

inactiveAssets.each { asset ->
    def assetName = asset.name
    def communityId = asset.communityId
//Store the asset details in a list
    listAssetDetails << [
            assetId: asset.id,
                    assetName: assetName,
            communityId: communityId
    ]
}
loggerApi.info("Inactive asset details: ${listAssetDetails}")
