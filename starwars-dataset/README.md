# Star Wars Dataset

### A simple relational model of Star-Wars data in structured text format.

<p align="center">
<img title="mikenakis-starwars-dataset logo" src="starwars-dataset.svg" width="256"/><br/>
The mikenakis-starwars-dataset logo, <i>R2D2</i><br/>
Created by Arthur Shlain from the Noun Project, used under <a href="https://creativecommons.org/licenses/by/3.0/us/">CC BY</a> license<br/>
</p>

When writing general purpose software that deals with the storage, retrieval, and manipulation of data, 
(say, when writing an ORM,) 
it is convenient to have a dataset for testing purposes 
which strikes a good balance between complexity and familiarity.
- Complexity means that the dataset should be rich, and it should contain all different kinds of relationships, so that all different modes of operation of the software can be tested.
- Familiarity means that the developer should already know most of the subject matter by heart, so that any errors that may occur can easily be spotted. 

The Star Wars universe dataset does strike such a good balance: 
it is relatively rich, and most of us are intimately familiar with it.  

(Of course, it is a cultural thing, as well as a personal preference thing, 
so there will inevitably be people out there who are not familiar at all with the Star Wars universe, 
and I do not think the slightest bit less of them, 
I am actually quite close to being personally fed-up with Star Wars, 
but the truth remains that most people know a big part of it by heart.)

The format exposed by this library is very simple:

- there is a `TextDomain` object containing a list of `TextTable`
- each `TextTable` is a list of `TextRow`.
- each `TextRow` has:
	- an `entity-id` of type `int`
	- a `Map<String,String>` mapping value field names to field values,
	- a `Map<String,List<Integer>>` mapping relation field names to `entity-id`s.

The data has been downloaded from a web api, and it contains many mistakes, 
so there is also a `SwapiFixer` class which fixes them.

The test loads the model, fixes it, and dumps everything to the console.
(So, it is not really a test, 
it is more of an exercise of the entire mechanism, 
with results observable on the console.)

The original source of information used to be http://swapi.co (does not exist anymore.) 
I used to store the original raw data (before the fixing stage was applied)
in the form of text files under `$(user.home)/Documents/Swapi/`, 
and if one of the text files was missing while the library was loading the model, 
the library would download the data from the web API.

However, at some point circa 2020 swapi.co went belly-up.

Then someone created https://swapi.dev to replace swapi.co, 
but when I last checked (on 2021-05-18)
I found that swapi.dev has at least a couple of serious issues:

- It has one less film entity, while it should have at least one more.
- It has lost the contents of the "species" field of several person entities.

Note that I have not checked everything, so there may be more issues with swapi.dev, but the issues that I found
were already enough to warrant not using it, so I did not check further.

As a result, I am not downloading data from the web API anymore, and instead the text files that were once downloaded from swapi.co now live as resources within the project.

The old code that used to do the downloading is still there, but it is not meant to be reachable anymore, and if it is somehow reached, it will intentionally throw an exception.
