# Tilepic


### Library to display images without changing the relationship of the parties.

Here we got.

![Screenshot]()

## Usage

### Layout

``` java

// Create list view
ListView list = (ListView) findViewById(R.id.listview);

// Create list of string 
List<String> urls = new ArrayList<>();

// Put urls to "urls" list
for (int i = 0; i < 10; i++) {
    urls.put("your/url/from/something.*");
}

// Use lib to display all images
Tilepic.with(context).put(urls).into(listview);

```

## About

Tilepic actually works, but a lot functions in progress.

All pics are random gen with special service