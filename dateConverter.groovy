//**********This script reformats the date received in a Collibra form to dd-MM-yyyy for the reviewer to see in a text-display. 

//the date input received: 1732233600000 (epoch format)
//date output required: dd.MM.yyyy

import java.text.SimpleDateFormat
import java.util.TimeZone

//convert epoch to date object
def startDateObj = new Date(startDate)
def endDateObj = new Date(endDate)

def dateFormat = new SimpleDateFormat("dd-MM-yyyy")

dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"))

def formattedStartDate = dateFormat.format(startDateObj)
def formattedEndDate = dateFormat.format(endDateObj)

loggerApi.info("Start Date (as Date): ${startDateObj}")
loggerApi.info("End Date (as Date): ${endDateObj}")

loggerApi.info("Formatted Start Date: ${formattedStartDate}")
loggerApi.info("Formatted End Date: ${formattedEndDate}")


execution.setVariable("formattedStartDate", formattedStartDate)
execution.setVariable("formattedEndDate", formattedEndDate)