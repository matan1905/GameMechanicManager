# Game Mechanic Manager

Java library that allows you to build your gameplay mechanics in a modular way.
You could also download other people's mechanics and use them to quickly prototype a game with common mechanics

## Getting Started

These instructions will get you a copy of the project up and running on your local machine for development and testing purposes. See deployment for notes on how to deploy the project on a live system.

### Prerequisites

What things you need to install the library and how to install them

```
* Basic understanding of Java
* knowledge to import jar into a project
```

### Installing

A step by step series of examples that tell you have to get a development env running

[download and import the project's jar file](https://github.com/matan1905/GameMechanicManager/releases)

Check that you can instantiate the main class object:
```
GameMechanicManager gmm = new GameMechanicManager(null,null);//Ignore the null for now
```


## Contributing

Please read [CONTRIBUTING.md](https://github.com/matan1905/GameMechanicManager/blob/master/CONTRIBUTING.md) for details on our code of conduct, and the process for submitting pull requests to us.

## Versioning

We use [SemVer](http://semver.org/) for versioning. For the versions available, see the [tags on this repository](https://github.com/matan1905/GameMechanicManager/tags). 


## License

This project is licensed under the MIT License - see the [LICENSE.md](LICENSE.md) file for details

## Inspiration
Two main reasons I decided to build this library:

*  Everytime I had a cool new idea for a game mechanic I had to build a whole game from almost scratch and the thought of implementing
so many mechanics (Especially for RPG games) really destroyed my motivation

* People have amazing ideas about very specific things(see mod communities for example), if we could make game mechanics modular
enough, people could have an incredible inventory system or a quest system with just a few lines of codes, pushing all of their focus
to whatever makes their game so great.
