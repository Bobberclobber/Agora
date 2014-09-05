My Android project for the course TDDD80 - Mobila och Sociala Applikationer (Mobile and Social Applications) the spring and 
summer of 2014. 
The application is built using the android OS in combination with a server built using python in combination
with Flask and SQLite3. 
The purpose of the appication is to create a mobile and social environment in which the users can easily share any project
ideas they may have on their mind or find and contact other people with interesting ideas.

NOTE: The first (alpha) launchable (though untested) version (completed 20/08/2014) was completed using Androd Studio 8.6.0 with a separately downloaded SDK. 
	  Starting android studio using this SDK resulted in a bunch of errors which were solved using the IDE's own solutions, hopefully this will work on other machines as well.

Post-Alpha Update Log:

	26-08-2014:
		The server-side of the application has been deployed to openshift. It has not been tested though.

	27-08-2014:
		The application has gone through one iteration of user testing. Suggested improvements resulting from this test will be added under the Known Errors- or TODO-section.
		A few own tests prior to the actual user test revealed that additional code was needed on the client side of the application in order for it to work properly with the server.

	28-08-2014:
		A few bug-fixes have been applied. A loading dialog no longer appears when using android's built-in back arrow to a search result activity.
		Several bugs where the number of followers where displayed incorrectly when logging in as a new user on the same phone have been corrected.
		All code required to provide server support in the client has been added. There are still some problems with the server though.

Known Errors:
	> Trying to register a new user with an image string which is too long, it causes the server to raise error 404. 
	  The cause of this is unknown as it works properly if the string is shortened.
	> Calling any function in the server code which uses the marshal.loads()-function raises a ValueError which in turn leads to error 500 to be raised. 
	  The cause is unknown as the same code when run on localhost works perfectly. 

TODO:
	> Improve the back-navigation with a back-stack so that the user won't end up in an activity which is
	  several steps further back than the one which was active before the current one
	> Add a preview feature
	> Rework the current tag system. Examples of how:
		- Remove the requirement to type # before the tags
		- Add a list of common categories and make these searchable
	> Tie the app together and make it easier to find ideas by adding some sort of universal idea feed / a feed with the most liked ideas of the day or similar.
	> Rename every occurance of "approve", "approval", "un-approve" and "un-approval" to "like" and "stop liking" / "like" and "dislike"
	> Make everything more obvious. Maybe add a button to list objects or some indication that the object is clickable
	> Remove all features  and transitions which require a click on a profile picture, it is way to unintuitive
	> Add support for sharing content on other social media
	> Add a page which holds the app together, perhaps a page with the aforementioned universal idea feed / most liked idea feed
	> Add support for writing a title for the ideas posted.
