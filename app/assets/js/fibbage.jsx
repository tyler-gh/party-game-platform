var STATES = {
    INDEX: "index",
    WAITING: "waiting",
    PICKING: "picking",
    LIEING: "lieing",
    GUESSING: "guessing",
    AWARDING: "awarding"
};

var ACTIONS = {
    NEW_CLIENT: "new-client",
    START_PICKING: "start-picking",
    START_LIEING: "start-lieing",
    SUBMIT_LIE: "submit-lie",
    SUBMIT_GUESS: "submit-guess"
};

var Lie = function (id, lie) {
    this.lie = lie;
    this.id = id;
};

var User = function (id, name) {
    this.id = id;
    this.name = name;
    this.points = 0;
};

function getCookie(name) {
    var value = "; " + document.cookie;
    var parts = value.split("; " + name + "=");
    if (parts.length == 2) return parts.pop().split(";").shift();
}

var api = new Rest("localhost", 9000);

var WaitingForPlayers = React.createClass({
    render: function () {
        var usersString = JSON.stringify(this.props.users);
        return (
            <div>
                {usersString}
                <br/>
                <br/>
                <form className="startGameForm" onSubmit={this.props.startGame}>
                    <input type="submit" value="Start Game"/>
                </form>

            </div>
        );
    }
});

var JoinForm = React.createClass({
    getInitialState: function () {
        return {name: '', gameCode: ''};
    },
    handleNameChange: function (e) {
        this.setState({name: e.target.value});
    },
    handleGameCodeChange: function (e) {
        this.setState({gameCode: e.target.value});
    },
    handleSubmit: function (e) {
        e.preventDefault();
        var name = this.state.name.trim();
        var gameCode = this.state.gameCode.trim();
        if (!name || !gameCode) {
            return;
        }
        var props = this.props;
        api.one("join_game", "fibbage").one(gameCode).one(name).post(function (data) {
            console.log("joined_game");
            console.log(data);
            props.setUser(data);
            data.users.forEach(function (user) {
                props.addUser(user);
            });
            props.setPage(STATES.WAITING);
        });
        this.setState({name: '', gameCode: ''});
    },
    render: function () {
        return (
            <form className="joinForm" onSubmit={this.handleSubmit}>
                <input
                    type="text"
                    placeholder="Your Name"
                    value={this.state.name}
                    onChange={this.handleNameChange}
                />
                <br/>
                <input
                    type="text"
                    placeholder="Game Code"
                    value={this.state.gameCode}
                    onChange={this.handleGameCodeChange}
                />
                <br/>
                <input type="submit" value="Join Game"/>
            </form>
        );
    }
});

var CreateForm = React.createClass({
    handleSubmit: function (e) {
        var props = this.props;
        e.preventDefault();
        api.one("create_game", "fibbage").post(function (data) {
            console.log(data);
            props.setUser(data);
            props.addUser(data);
            props.setPage(STATES.WAITING);
        }, function (text, status) {
            alert("The request did not succeed!\n\nThe response status was: " + status + " " + text + ".");
        });
    },
    render: function () {
        return (
            <form className="createForm" onSubmit={this.handleSubmit}>
                <input type="submit" value="Create Game"/>
            </form>
        );
    }
});

var Index = React.createClass({
    render: function () {
        return (
            <div>
                <br/>
                <CreateForm setPage={this.props.setPage} addUser={this.props.addUser} setUser={this.props.setUser}/>
                <br/>
                <JoinForm setPage={this.props.setPage} addUser={this.props.addUser} setUser={this.props.setUser}/>
            </div>
        )
    }
});

var EnterLie = React.createClass({
    getInitialState: function () {
        return {lie: ''};
    },
    handleLieChange: function (e) {
        this.setState({lie: e.target.value});
    },
    handleSubmit: function (e) {
        e.preventDefault();
        var lie = this.state.lie.trim();
        if (!lie) {
            return;
        }
        this.props.submitLie(lie.toUpperCase());
    },
    render: function () {
        return (
            <div>
                {this.props.question.question}
                <form className="lieForm" onSubmit={this.handleSubmit}>
                    <input
                        type="text"
                        placeholder="Enter Your Lie"
                        value={this.state.lie}
                        onChange={this.handleLieChange}
                    />
                    <br/>
                    <input type="submit" value="Lie"/>
                </form>
            </div>
        );
    }
});

var PickQuestion = React.createClass({
    pickQuestion: function (e) {
        console.log(e.target.value);
        this.props.pickQuestion(this.props.questions.find(function (q) {
            return q.category == e.target.value;
        }));
    },
    render: function () {
        console.log(this.props.questions);
        var me = this;
        return <div>
            {
                this.props.user.id == this.props.currentUser ?
                    this.props.questions.map(function (question) {
                        return <button onClick={me.pickQuestion} value={question.category}>{question.category}</button>
                    }) :
                "Waiting on " + this.props.users[this.props.currentUser].name
            }
        </div>
    }
});

var MakeGuess = React.createClass({
    makeGuess: function (e) {
        console.log(e.target.value);
        if (e.target.value == this.props.question.answer) {
            this.props.submitGuess({correct: true, guess: this.props.question});
        } else {
            var guess = this.props.lies.find(function (lie) {
                return lie.lie == e.target.value;
            });
            this.props.submitGuess({correct: false, guess: guess});
        }
    },
    render: function () {
        console.log(this.props.lies);
        console.log(this.props.question);
        var guesses = this.props.lies.map(function (lie) {
            return {correct: false, guessObj: lie, guess: lie.lie};
        });
        guesses.push({correct: true, guessObj: this.props.question, guess: this.props.question.answer});

        function shuffle(o) {
            for (var j, x, i = o.length; i; j = Math.floor(Math.random() * i), x = o[--i], o[i] = o[j], o[j] = x);
            return o;
        }

        guesses = shuffle(guesses);
        var me = this;
        return <div>
            {
                guesses.map(function (guess) {
                    return <button onClick={me.makeGuess} value={guess.guess}>{guess.guess}</button>
                })
            }
        </div>
    }
});

var Awarding = React.createClass({
    render: function () {
        return <div>
            {JSON.stringify(this.props.guesses)}
            <br/>
            <br/>
            {JSON.stringify(this.props.users)}
        </div>;
    }
});

var timeoutID = 0;

var App = React.createClass({
    getInitialState: function () {
        return {
            user: null,
            users: [],
            stage: 1,
            currentUser: 1,
            questions: [],
            question: "",
            lies: [],
            guesses: [],
            page: STATES.INDEX
        };
    },
    reset: function() {
        this.setState({
            questions: [],
            question: "",
            lies: [],
            guesses: []
        });
    },
    processWSData: function (data) {
        var actionType = data.actionType;
        switch (actionType) {
            case ACTIONS.NEW_CLIENT:
                this.addUser(new User(data.client.id, data.client.name));
                console.log(data.client);
                break;
            case ACTIONS.START_PICKING:
                this.setPage(STATES.PICKING, {questions: data.data.questions});
                console.log("Starting Game!!!");
                break;
            case ACTIONS.START_LIEING:
                this.setPage(STATES.LIEING, {question: data.data.question});
                console.log("Starting Lieing!!!");
                break;
            case ACTIONS.SUBMIT_LIE:
                this.addLie(new Lie(data.client.id, data.data.lie));
                if (this.state.lies.length == this.state.users.length - 1) { // TODO FIX if no monitor view
                    this.setPage(STATES.GUESSING);
                }
                break;
            case ACTIONS.SUBMIT_GUESS:
                // TODO put this into it's own class
                this.addGuess(data);
                if (this.state.guesses.length == this.state.users.length - 1) { // TODO FIX if no monitor view
                    this.setPage(STATES.AWARDING);
                }
                break;
        }
    },
    setUser: function (user) {
        this.setState({user: new User(user.id, user.name)});
    },
    addUser: function (user) {
        this.setState({users: this.state.users.concat([new User(user.id, user.name)])});
    },
    addLie: function (lie) {
        this.setState({lies: this.state.lies.concat([new Lie(lie.id, lie.lie)])});
    },
    addGuess: function (guess) {
        this.setState({guesses: this.state.guesses.concat([guess])});
    },
    setPage: function (page, state) {
        if (page != this.state.page) {
            this.setState({page: page});
            if (state) {
                this.setState(state);
            }
        }
    },
    startGame: function (e) {
        e.preventDefault();
        if (this.state.users.length < 2) {
            return;
        }
        this.startPicking();
    },
    startPicking: function () {
        if(this.state.page == STATES.WAITING || this.state.page == STATES.AWARDING) {
            if(timeoutID) {
                clearTimeout(timeoutID);
            }
            this.reset();
            function getRandomInt(max) {
                return Math.floor(Math.random() * (max + 1));
            }

            var questions = [];

            for (var i = 0; i < 5; i++) {
                questions.push(QUESTIONS[getRandomInt(QUESTIONS.length)]);
            }

            api.socketSend("ws", JSON.stringify({
                client: {
                    name: this.state.user.name,
                    id: this.state.user.id
                },
                actionType: ACTIONS.START_PICKING,
                data: {
                    questions: questions
                }
            }));
        }
    },
    startLieing: function (question) {
        api.socketSend("ws", JSON.stringify({
            client: {
                name: this.state.user.name,
                id: this.state.user.id
            },
            actionType: ACTIONS.START_LIEING,
            data: {
                question: question
            }
        }));
    },
    submitLie: function (lie) {
        api.socketSend("ws", JSON.stringify({
            client: {
                name: this.state.user.name,
                id: this.state.user.id
            },
            actionType: ACTIONS.SUBMIT_LIE,
            data: {
                lie: lie
            }
        }));
    },
    submitGuess: function (guess) {
        api.socketSend("ws", JSON.stringify({
            client: {
                name: this.state.user.name,
                id: this.state.user.id
            },
            actionType: ACTIONS.SUBMIT_GUESS,
            data: {
                guess: guess
            }
        }));
    },
    render: function () {
        var page;
        var me = this;
        switch (this.state.page) {
            case STATES.WAITING:
                if (api.socketClosed("ws")) {
                    api.socket(
                        "ws",
                        function (evt) {
                            console.log("connected!!");
                        },
                        function (evt) {
                        },
                        function (evt) {
                            me.processWSData(JSON.parse(evt.data));
                        },
                        function (evt) {
                        }
                    );
                }
                page = <WaitingForPlayers setPage={this.setPage} users={this.state.users} startGame={this.startGame}/>;
                break;
            case STATES.PICKING:
                page = (
                    <PickQuestion
                        questions={this.state.questions}
                        user={this.state.user}
                        users={this.state.users}
                        currentUser={this.state.currentUser}
                        pickQuestion={this.startLieing}
                    />
                );
                break;
            case STATES.LIEING:
                page = <EnterLie question={this.state.question} submitLie={this.submitLie}/>;
                break;
            case STATES.GUESSING:
                page = (
                    <MakeGuess
                        lies={this.state.lies}
                        question={this.state.question}
                        submitGuess={this.submitGuess}
                    />
                );
                break;
            case STATES.AWARDING:
                console.log(me.state.stage);
                this.state.guesses.forEach(function(guess) {
                    console.log(guess);
                   if(guess.data.guess.correct) {
                       var user = me.state.users[guess.client.id];
                       user.points += (1000 * me.state.stage);
                   }
                });

                page = <Awarding guesses={this.state.guesses} users={this.state.users}/>;
                timeoutID = setTimeout(function() {
                    me.setState({currentUser: me.state.currentUser + 1});
                    if(me.state.currentUser == me.state.users.length) {
                        me.setState({currentUser: 1, stage: me.state.stage + 1});
                    }
                    me.startPicking();
                }, 10000);
                break;
            case STATES.INDEX:
            default:
                page = <Index setPage={this.setPage} addUser={this.addUser} setUser={this.setUser}/>;
                break;
        }
        return (
            <div>{page}</div>
        );
    }
});

ReactDOM.render(
    <App />,
    document.getElementById('pg-app')
);

var QUESTIONS = [
    {
        "category": 8,
        "question": "~~8-0 is the obscure emoticon that stands for _____.",
        "answer": "BAD HAIR DAY"
    },
    {
        "category": 107,
        "question": "Monroe Isadore, a 107-year-old man from Arkansas, died during a _____.",
        "answer": "SHOOTOUT"
    },
    {
        "category": 55000,
        "question": "Owning 55,000 of them, Ted Turner has the world's largest private collection of _____.",
        "answer": "BISON"
    },
    {
        "category": "5% MORE MILK",
        "question": "A study published in the journal Anthrozoo reported that cows produce 5% more milk when they are given _____.",
        "answer": "NAMES"
    },
    {
        "category": "AFGHANISTAN",
        "question": "In 2002, Bruce Willis sent 12,000 boxes of _____ to U.S. soldiers in Afghanistan.",
        "answer": "GIRL SCOUT COOKIES"
    },
    {
        "category": "ALFRED",
        "question": "The electric chair was invented by a professional _____ named Alfred Southwick.",
        "answer": "DENTIST"
    },
    {
        "category": "ARRESTED",
        "question": "A man in L.A. was arrest at a police-sponsored gun buyback when, instead of a gun, he tried to sell the cops a _____.",
        "answer": "PIPE BOMB"
    },
    {
        "category": "BAD",
        "question": "CELEBRITY TWEET! 9:43 PM - 1 Oct 13 @MikeTyson tweeted, \"I'm a bad ____.:",
        "answer": "BOWLER"
    },
    {
        "category": "BALLS",
        "question": "Only about 14 games were ever made for the Gizmondo game system including the game \"_____ Balls.\"",
        "answer": "STICKY"
    },
    {
        "category": "BLOWING",
        "question": "On November 12, 1970, George Thornton, a highwat engineer in Oregon, had the unusual job of blowing up a _____.",
        "answer": "DEAD WHALE"
    },
    {
        "category": "BOATS",
        "question": "People in Damariscotta, Maine hold an annual race where they use _____ as boats.",
        "answer": "PUMPKINS"
    },
    {
        "category": "BOXING",
        "question": "Leo Granit Kraft is a world champion in an unusual sport that combines boxing and _____.",
        "answer": "CHESS"
    },
    {
        "category": "BRANSON",
        "question": "Andrew Wilson, a man from Branson, Missouri, legally changed his name to simply _____.",
        "answer": "THEY"
    },
    {
        "category": "BUENOS AIRES",
        "question": "As a young student in Buenos Aires, Pope Francis worked as a _____.",
        "answer": "BOUNCER"
    },
    {
        "category": "BUTTOCKS",
        "question": "Dasypygal is an adjective meaning \"having _____ buttocks.\"",
        "answer": "HAIRY"
    },
    {
        "category": "CALLUS",
        "question": "A woman in Muncie, Indiana was hospitalized after trying to remove a callus on her foot with a _____.",
        "answer": "SHOTGUN"
    },
    {
        "category": "CALVARY",
        "question": "Sponsored by the Calvary Lutheran Church, people in Fort Worth Texas can now attend the unconventional Church-in-a-_____.",
        "answer": "PUB"
    },
    {
        "category": "CHARLES BUKOWSKI",
        "question": "Famed American poet Charles Bukowski's tombstone is engraved with the words \"Don't _____.\"",
        "answer": "TRY"
    },
    {
        "category": "CRUISES",
        "question": "Jacobite Cruises purchased unusual insurance to protect it from damage caused by _____.",
        "answer": "THE LOCH NESS MONSTER"
    },
    {
        "category": "DEVIL",
        "question": "El Colacho is a Spanish festival where people dress up like the devil and jump over _____.",
        "answer": "BABIES"
    },
    {
        "category": "DIRTY",
        "question": "Utah's Lehi City Council approved a request to change the name of _____ Road because it sounded sort of dirty.",
        "answer": "MORNING GLORY"
    },
    {
        "category": "DRESSES",
        "question": "Cheap Chic Weddings is an annual contest in which participants make wedding dresses out of _____.",
        "answer": "TOILET PAPER"
    },
    {
        "category": "DUCK",
        "question": "Marcella Hazan is the culinary guru who pioneered the unusual technique of cooking duck with a _____.",
        "answer": "HAIR DRYER"
    },
    {
        "category": "E.T.",
        "question": "The sound of E.T. walking was made by someone squishing _____.",
        "answer": "JELL-O"
    },
    {
        "category": "EBAY",
        "question": "The first item listed on eBay was a broken _____.",
        "answer": "LASER POINTER"
    },
    {
        "category": "EINSTEIN",
        "question": "Oddly enough, Albert Einstein's eyeballs can be found in a _____ in New York City.",
        "answer": "SAFE DEPOSIT BOX"
    },
    {
        "category": "ERECTED",
        "question": "In 2013, a wealthy Michagan man bought the house next to his ex -wife and erected a giant bronze statue of a _____.",
        "answer": "MIDDLE FINGER"
    },
    {
        "category": "FACIAL",
        "question": "For $180, the Shizuka New York skin care salon offeres the unusual but traditional Japanese facial that is part rice bran and part _____.",
        "answer": "BIRD POOP"
    },
    {
        "category": "FINAL FIBBAGE",
        "question": "The shape of wombat poop.",
        "answer": "CUBE"
    },
    {
        "category": "FINAL FIBBAGE",
        "question": "What the British call the dance that Americans call the \"Hokey Pokey.\"",
        "answer": "HOKEY COKEY"
    },
    {
        "category": "FINAL FIBBAGE",
        "question": "The band Queen's original name.",
        "answer": "SMILE"
    },
    {
        "category": "FINAL FIBBAGE",
        "question": "The name of the first chimp sent into space.",
        "answer": "HAM"
    },
    {
        "category": "FINAL FIBBAGE",
        "question": "The name for a group of porcupines.",
        "answer": "PRICKLE"
    },
    {
        "category": "FINAL FIBBAGE",
        "question": "The name of the dog that won the 2012 World's Ugliest Dog Competition.",
        "answer": "MUGLY"
    },
    {
        "category": "FINAL FIBBAGE",
        "question": "Dr. Suess is credit with coining this common derogatory term in his 1950 book If I Ran the Zoo.",
        "answer": "NERD"
    },
    {
        "category": "FINAL FIBBAGE",
        "question": "Harry Houdini once threatened to shoot all of these people.",
        "answer": "PSYCHICS"
    },
    {
        "category": "FINAL FIBBAGE",
        "question": "The original name for the search engine that became Google.",
        "answer": "BACKRUB"
    },
    {
        "category": "FINAL FIBBAGE",
        "question": "Miley Cyrus's real first name.",
        "answer": "DESTINY"
    },
    {
        "category": "FINAL FIBBAGE",
        "question": "The name of the dildo resembling Queen Elizabeth II that's sold by the Company Masturpieces.",
        "answer": "BUCKINGHAM PHALLUS"
    },
    {
        "category": "FINAL FIBBAGE",
        "question": "Michael J. Fox's middle name.",
        "answer": "ANDREW"
    },
    {
        "category": "FINAL FIBBAGE",
        "question": "The name of the Star Trek starship in creator Gene Roddenberry's original script.",
        "answer": "YORKTOWN"
    },
    {
        "category": "FINAL FIBBAGE",
        "question": "At 65% alcohol by volume, Brewmesiter brews the world's strongest beer, which goes by this name.",
        "answer": "ARMAGEDDOM"
    },
    {
        "category": "FINAL FIBBAGE",
        "question": "The name of the man on the Quaker Oats label.",
        "answer": "LARRY"
    },
    {
        "category": "FIRE",
        "question": "During a famous fire in 1567, a Norwegian man named Hans Steininger died after tripping over a _____.",
        "answer": "BEARD"
    },
    {
        "category": "FISHING",
        "question": "The fishing company E21 makes a very peculiar fishing rod that is composed of 70% _____.",
        "answer": "CARROTS"
    },
    {
        "category": "FLIGHT",
        "question": "The first reporting on the Wright .rothers' flights appeared not in a newspaper or on radio, but in a small journal dedicated to the topic of _____.",
        "answer": "BEEKEEPING"
    },
    {
        "category": "FOOD",
        "question": "There's a novelty museum in Arlington, Massachusetts that only collects food that has been _____.",
        "answer": "BURNT"
    },
    {
        "category": "GALLAGHER",
        "question": "Prop comic Gallagher, known for smashing watermelons with a sledgehammer, earned a college degree in the field of _____.",
        "answer": "CHEMICAL ENGINEERING"
    },
    {
        "category": "GANG",
        "question": "In 1976 boxing legend Muhammad Ali released an education children's album titled \"Ali and His Gang Vs. Mr. _____.\"",
        "answer": "TOOTH DECAY"
    },
    {
        "category": "GIFT",
        "question": "On January 13, 2014, U.S. Secretary of State John Kerry presented to Russian Foreign Minister Sergei Lavrow the odd gift of two very large _____.",
        "answer": "POTATOES"
    },
    {
        "category": "GIN",
        "question": "In the 16th century, gin was referred to as the Mother's _____.",
        "answer": "RUIN"
    },
    {
        "category": "GLASTONBURY",
        "question": "During th mid to late-nineties, the English town of Glastonbury was on a manhunt for the old house intruder known as \"The _____.\"",
        "answer": "TICKLER"
    },
    {
        "category": "GROWLERS",
        "question": "According to the National Oceanic and Atmospheric Association, small icebergs are offically called \"growlers.\" Medium icebergs go by the strange, two-word name \"_____.\"",
        "answer": "BERGY BITS"
    },
    {
        "category": "HIP",
        "question": "Suffering from an extremely rare side effect after getting hip surgery in 2010, a Dutch man has alienated his family because he cannot stop _____.",
        "answer": "LAUGHING"
    },
    {
        "category": "HOUR",
        "question": "It's weird work but Jackie Samuel charges $60 an hour to _____.",
        "answer": "SNUGGLE"
    },
    {
        "category": "HUGGIES",
        "question": "Huggies Brazil developed a phone app that tells you when a baby's diaper is wet. It's called _____.",
        "answer": "TWEETPEE"
    },
    {
        "category": "ICE CREAM",
        "question": "Ben and Jerry only started making ice cream because it was too expensive to make _____.",
        "answer": "BAGELS"
    },
    {
        "category": "ILLINOIS",
        "question": "A spectator in an Illinois courtroom was sentenced to six months in jail for ______ during a trial.",
        "answer": "YAWNING"
    },
    {
        "category": "ILLINOIS",
        "question": "The sports teams at Freeport High School in Illinois are oddly named after an inanimate object. The teams are called the Freeport _____.",
        "answer": "PRETZELS"
    },
    {
        "category": "INVENTOR",
        "question": "The inventor of the laxative Ex-Lax has the unusual name Max _____.",
        "answer": "KISS"
    },
    {
        "category": "IOWA",
        "question": "Britt, Iowa annually crowns a King and Queen _____.",
        "answer": "HOBO"
    },
    {
        "category": "JAPAN",
        "question": "Tashirojima is an island off of Japan that is complete overrun by _____.",
        "answer": "CATS"
    },
    {
        "category": "JOHN LARROQUETTE",
        "question": "For his voiceover work in Texas Chainsaw Massacre, John Larroqueet was paid not with money, but with _____.",
        "answer": "MARIJUANA"
    },
    {
        "category": "JOSH",
        "question": "The fans of musician Josh Groban are called _____.",
        "answer": "GROBANITES"
    },
    {
        "category": "JOY",
        "question": "Jim Begelow, Ph.D., wrote a book called The Joy of Un_____!.",
        "answer": "CIRCUMCISING"
    },
    {
        "category": "JUNO",
        "question": "While president of the United States, John Adams had a dog named Juno and a dog named _____.",
        "answer": "SATAN"
    },
    {
        "category": "KICKSTARTER",
        "question": "A Kickstarter campaign met its $30,000 goal on April 7, 2012 for its shoes designed fo _____.",
        "answer": "ATHEISTS"
    },
    {
        "category": "KILL",
        "question": "In 2007, Golden Laurel Entertainment published a violent board game called Kill the _____.",
        "answer": "HIPPIES"
    },
    {
        "category": "LICK",
        "question": "In 2012, a 26-year-old man from London went on a mission to lick every _____ in the United Kingdom.",
        "answer": "CATHEDRAL"
    },
    {
        "category": "MARYLAND",
        "question": "Maryland's official state sport is _____.",
        "answer": "JOUSTING"
    },
    {
        "category": "MOROCCO",
        "question": "In 2003, Morocco made the highly unusual offer to send 2,000 _____ to assist the United States' war efforts in Iraq.",
        "answer": "MONKEYS"
    },
    {
        "category": "MUSSOLINI",
        "question": "Romano Mussolini, son of the fascist dictator Benito Mussolini, did not follow in his father's footsteps. Instead, he made his living as a _____.",
        "answer": "JAZZ MUSICIAN"
    },
    {
        "category": "MUSTACHE",
        "question": "Patented in 1890, U.S. Patent US435748 is for a mustache ____.",
        "answer": "GUARD"
    },
    {
        "category": "NEBRASKA",
        "question": "In 2007, to make a point, Nebraskain State Sen. Ernie Chambers filed a frivolous lawsuit against _____.",
        "answer": "GOD"
    },
    {
        "category": "NEPAL",
        "question": "ROAD TRIP! When in Nepal, visit the village of Parsawa and Laxmipur, where you can enjoy the slightly off-putting 10 day _____ Festival.",
        "answer": "CURSING"
    },
    {
        "category": "NEW HAMPSHIRE",
        "question": "A group known as the \"Robin Hooders\" in Keene, New Hampshire pay for other people's _____.",
        "answer": "PARKING METERS"
    },
    {
        "category": "NORWAY",
        "question": "In an effort to push \"slow TV,\" Norway had a 12-hour block of programming in 2013 dedicated to _____.",
        "answer": "KNITTING"
    },
    {
        "category": "OH",
        "question": "Belmont University in Nashville has offered a class called \"Oh, Look, a _____.\"",
        "answer": "CHICKEN"
    },
    {
        "category": "PACIFIC",
        "question": "The area in the Pacific Ocean where great white sharks congregate every spring is called the White Shark _____."
    },
    {
        "category": "PANTS",
        "question": "Reg Mellor is the reigning champion of a sport that just involves keeping a _____ in your pants.",
        "answer": "FERRET"
    },
    {
        "category": "PEPSI",
        "question": "During the summer of 2007, Pepsi sold a gree-tinted coal in Japan called Pepsi Ice _____.",
        "answer": "CUCUMBER"
    },
    {
        "category": "PETER",
        "question": "Under Peter the Great, noblemen had to pay 100 rubles a year for a _____ license.",
        "answer": "BEARD"
    },
    {
        "category": "PETITION",
        "question": "In 2000, Australia had its larget ever online petition, which called for an end to rising _____ prices.",
        "answer": "BEER"
    },
    {
        "category": "PIGS",
        "question": "Although very unconventional, farmer William von Schneidau feeds his pigs _____.",
        "answer": "MARIJUANA"
    },
    {
        "category": "PILE",
        "question": "When Paul Nelson and Andrew Hunter climbed Britain's highest mountain in 2006, they made an unusual discovery hidden behind a pile of stones. It was a _____.",
        "answer": "PIANO"
    },
    {
        "category": "POLICE",
        "question": "At 2:45 a.m. one day in June 2013, a man in Orlando, Florida was arrested for walking up to a police officer and punching his _____.",
        "answer": "HORSE"
    },
    {
        "category": "PRIZE",
        "question": "A 2013 Pakistani game show caused a controversy when their grand prize was a _____.",
        "answer": "BABY"
    },
    {
        "category": "PROM",
        "question": "In 2013, two teens from Sequoyah High Sxhool near Atlanta, Georgia won $5,0000 schoplarships for wearing _____ to prom",
        "answer": "DUCT TAPE"
    },
    {
        "category": "QUEENS",
        "question": "Since 2000, a couple in Queens, New York has been living rent-free in a _____ in exchange for taking care of it.",
        "answer": "CEMETERY"
    },
    {
        "category": "RATHER",
        "question": "For a story he was reporting on in 1955, Dan Rather tried _____ for the first time.",
        "answer": "HEROIN"
    },
    {
        "category": "RECKONING",
        "question": "Chosen, Shunned and Reckoning are all books in an unusual series about _____ vampires.",
        "answer": "AMISH"
    },
    {
        "category": "RELEASE",
        "question": "In 2013, a 51-year-old Swedish inmate broke out of prison a day before his scheduled release so he could go see a _____.",
        "answer": "DENTIST"
    },
    {
        "category": "RIOT",
        "question": "There is a riot police unit in the Russian town of Rostov-na-Donu that is comprised entirely of _____'",
        "answer": "TWINS"
    },
    {
        "category": "SADDAM",
        "question": "In 1980, Saddam Hussein was named an honorary citizen of _____.",
        "answer": "DETROIT"
    },
    {
        "category": "SEIZURES",
        "question": "A neurology professor at Albany Medical College documented in a 1991 medical journal that one of his patients had seizures when she heard _____.",
        "answer": "MARY HART'S VOICE"
    },
    {
        "category": "SIMMONS",
        "question": "CELEBRITY TWEET! 2:49 PM - 15 Nov 13 Richard Simmons' Twitter handle @TheWeightSaint tweeted: \"At the airport ____ everyone.\"",
        "answer": "HUGGING"
    },
    {
        "category": "SMUGGLE",
        "question": "In 2010, Customs officers on the Morway-Sweden border intercepted a truck trying to smuggle 28 tons of _____ from China.",
        "answer": "GARLIC"
    },
    {
        "category": "STABBED",
        "question": "In 2012, a teenager from Weslaco, Texas claimed the reason he stabbed his friend was because a _____ made him do it.",
        "answer": "OUIJA BOARD"
    }
];
