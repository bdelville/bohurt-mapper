![Connecting Fighters](http://hithredin.eu/img/jQuery-File-Upload-9.8.1/server/php/files/18319009_2290354884523915_8719594987608189811_o.jpg)


## Bohurt App

Implementing features useful for bohurt into a android app

#### v1 expected features
* List of Event sorted by 'cool to use' filters
* List of Clubs
* Save a event into the user's calendar or share to contacts
* Deeplinks
* Form to register an event
* Form to register a club
* Form to submit a data change request (wrong club, tournament cancelled)
* Page to vote for next features for v2
* Share the app button and link to store for voting


### Coding

To set-up the environment to build, you need:

A googleMap apiKey
* Create a file app/res/google_maps_api.xml
* Get you api key here: https://developers.google.com/maps/documentation/android-api/signup
* fill it with your content:
```
<resources>
    <string name="google_maps_key" translatable="false" templateMergeStrategy="preserve">
        YOUR_API_KEY
    </string>
</resources>
```



