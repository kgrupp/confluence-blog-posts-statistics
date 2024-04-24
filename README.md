# Confluence Blog Post Statistics

Generates blog post statistics for a specific set of spaces.

## How to execute

* Setup configuration file by add the configuration file to `src/main/resources/configuration.properties`
```properties
baseUrl=https://YOUR_INSTANCE.atlassian.net
userName=ATLASSIAN_USER_NAME
password=ATLASSIAN_PW_OR_DEVELOPER_TOKEN
```
* Let gradle build and execute the main function in Application.kt with a comma separated list of your space keys (e.g. `DEV,ABC`).

It will print user statistics in a json format like the following:

```json
[ {
  "user" : {
    "id" : "account-id",
    "name" : "Max Mustermann",
    "emailAddress" : "some-email-address"
  },
  "totalBlogPosts" : 65,
  "totalLikes" : 78,
  "popularBlogPosts" : [ {
    "id" : "blog-post-id-1",
    "title" : "Blog Post Title 1",
    "likeCount" : 15,
    "createdAt" : "2024-03-10T15:04:00.255Z",
    "link" : "https://YOUR_INSTANCE.atlassian.net/wiki/x/ABCD"
  }, {
    "id" : "blog-post-id-2",
    "title" : "Blog Post Title 2",
    "likeCount" : 11,
    "createdAt" : "2024-02-12T09:58:42.570Z",
    "link" : "YOUR_INSTANCE/wiki/x/EFGH"
  }, {
    "id" : "blog-post-id-3",
    "title" : "Blog Post Title 3",
    "likeCount" : 9,
    "createdAt" : "2024-04-11T11:37:57.602Z",
    "link" : "YOUR_INSTANCE/wiki/x/IJKL"
  } ]
}, {
  // and more entries
} ]
```