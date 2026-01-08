# Use Cases

- I want to find transcribed songs by search using various criteria:
    - song name
    - album name
    - artist
    - publication name


- I want to browse transcribed songs by publication; optionally applying filters for date of publication and 
publication type.  _E.g. display all publication covers in chronological order of publication.  Drilling into the
publication displays the transcribed songs._


- I want to browse transcribed songs by _performance_ artist.  Drilling in displays transcribed songs performed by that
artist.

- I want to browse transcribed songs by album, sorted by the primary album artist.  Drilling in displays transcribed songs
from that album.


# Defined Use Cases Analysis

Transcribed Songs are the primary data element.  The _browse_ use cases all effectively provide a means of searching for
songs indirectly.  E.g., Browsing by publication — you would fetch (a filtered) list of publications and display.  Clicking 
on a publication would result in a song search with criteria of the clicked publication applied.

## Implications
- Define `songSearch` query to return Song data: 
  - Must minimally support filtering by: song title, publication, artist and album
  - Should return a song-centric data set:  List of Songs > Transcriptions > Publication
  - Should support cursor-based paging
- Define `pubSearch` query to return Publication list:
  - Must minimally support filtering by: pub type, (partial) pub name, date range (perhaps just years)
  - Should return publication level information only, of publications that have transcribed songs meeting the criteria
  - Should return publications in chronological order
- Define `artistSearch` query to return Artist list:
  - Must minimally support filtering by: (partial) name, artist role (song by: words, music, or performed; or album artist)
  - Should return artist level information only, of artists that are associated with transcribed songs meeting the criteria
  - Should return artists in alphabetical order
- Define `albumSearch` query to return Album list:
  - Must minimally support filtering by: (partial) title
    - Additional filtering: releaseDate (details), (partial) album artist name
  - Should return album level information only, of albums that are associated with transcribed songs meeting the criteria
  - Should return albums in either title alphabetical order or album artist alphabetical order; secondary by chronological 
    release date, if available.