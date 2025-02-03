# Confluence Blog Post Statistics

Generates blog post statistics for a specific set of spaces.

## How to execute

* Setup configuration file by add the configuration file to `src/main/resources/configuration.properties`

```properties
baseUrl=https://YOUR_INSTANCE.atlassian.net
userName=ATLASSIAN_USER_NAME
password=ATLASSIAN_PW_OR_DEVELOPER_TOKEN
```

* Let gradle build and execute the main function in Application.kt with a comma separated list of your space keys and the minimum created date (e.g.
  `DEV,ABC 2024-01-01`).
* As optional third parameter add the maximum created date (e.g. `DEV,ABC 2024-01-01 2024-12-31`).
* As optional forth parameter add the maximum created date (e.g. `DEV,ABC 2024-01-01 2024-12-31 2024-11-01`).

### Markdown Output

And additionally it will create a markdown formatted file (`statistics.md`). You can copy it into the confluence cloud editor to visualize the statistics.

See example output [here](statistics-example.md).

### JSON Output

It will print user statistics in a json format like the following:

```json
[
  {
    "user": {
      "id": "account-id",
      "name": "Max Mustermann",
      "emailAddress": "some-email-address"
    },
    "totalBlogPosts": 65,
    "totalLikes": 78,
    "popularBlogPosts": [
      {
        "id": "blog-post-id-1",
        "title": "Blog Post Title 1",
        "likeCount": 15,
        "createdAt": "2024-03-10T15:04:00",
        "link": "https://YOUR_INSTANCE.atlassian.net/wiki/x/ABCD"
      },
      {
        "id": "blog-post-id-2",
        "title": "Blog Post Title 2",
        "likeCount": 11,
        "createdAt": "2024-02-12T09:58:42",
        "link": "https://YOUR_INSTANCE.atlassian.net/wiki/x/EFGH"
      },
      {
        "id": "blog-post-id-3",
        "title": "Blog Post Title 3",
        "likeCount": 9,
        "createdAt": "2024-04-11T11:37:57",
        "link": "https://YOUR_INSTANCE.atlassian.net/wiki/x/IJKL"
      }
    ]
  },
  {
    // and more entries
  }
]
```

Additionally, it will save the file to `visualize/src/data/statistics.json`.
For details how to generate a static html page based on that see [visualize/README.md](visualize/README.md).
