# A.I.-Agent-Neo

The year is 2200 and the machines took over the world after a long and grueling battle
the likes of which were never seen before. Most of humanity was defeated and they got
their memories erased and then put into deep sleep. In this state, their brains are used
as a huge neural network by the machines. To harness the power of the humans’ brains,
the machines created a simulation called the matrix where humans who are asleep think
that in fact they are alive in. After the war, a very few number of humans were able to
escape the wrath of the machines and they started planning how to avenge their loss and
save the human race. A prophecy was told that there will come someone from within the
matrix who will be the chosen savior of humanity. This person is called Neo. The living
were able to get to the matrix and they were able to locate Neo and get him finally
awake. Now, Neo get in and out of the matrix when he can to save other humans.
The machines started noticing a weird pattern of behavior in the matrix and hence they
created a virus called agent Smith that should track Neo and kill him whenever he sees
him in the matrix. In order to do so, agent Smith made a lot of copies of himself and
decided to set a trap for Neo. They (the copies of agent Smith) took some of the humans
Neo would save as hostages and injected them with a slow spreading chemical that would
eventually kill them and turn them into agents. If a human dies in the matrix their brain
stops and they die in real life. Hence, Neo, on sensing that some humans are dying, rushed
to the matrix in order to save the humans. The agents do not know that Neo has special
skills and that he foresaw the agents’ plan. As such, Neo planted a number of pills that
would restore his health and the health of the hostages once activated and a number
of launching pads that will let him fly from one to the other. Also, Neo (on carrying
a hostage) can stop the spread of the virus from turning the hostages into agents upon
dying. However, he can not stop them from dying.
Hence, in order to limit the influence of the machines, Neo’s goal is to take as many
humans as possible alive to a special location called the telephone booth where they can
get out of the matrix. However, if hostages die and turn into agents, Neo would have to
kill these agents. Further, if a hostage dies while being carried by Neo, Neo would also
return it to the telephone booth so that the body would not become an agent in the future.
The area the hostages are held in can be thought of as an m × n grid of cells where
5 ≤ m, n ≤ 15. Initially, a grid cell is either free or contains one of the following: Neo,
a hostage, a pill, a pad, an agent, or the telephone booth. It is also worth noting that
initially there will not be any overlapping in a single cell. Meaning, a single cell will
contain only one type of object. Put simply, there will not be a case where Neo starts
from the same place as the telephone booth, or a hostage is at the same cell as a pill
and so on. Neo can not be at the same cell as an agent but can enter all other cells.
In this project, you will use search to help Neo complete his mission. Neo’s mission is
to end up at the telephone booth with all hostages that did not turn agents
returned (whether dead or alive) and, in the case hostages die and turn into
agents, each hostage that turned into an agent killed.
Neo can:
• Move in all four directions as long as there are no agents in the cell Neo is
heading towards.
• Carry a hostage only if both of them are in the same cell. Once a hostage is carried,
it can only be dropped in the telephone booth.
• Drop all hostages currently being carried at the telephone booth only if Neo is in
the same cell where the telephone booth lies.
• kill all agents at neighboring cells to Neo. Neighboring cells are the top, the
bottom, the left, and the right cells. Diagonal cells are not neighboring cells.
• Take a pill to increase his health and the health of all currently living hostages.
A pill could be taken only once.
• Fly from one launching pad to the next.
Neo can only carry up to c hostages at a time. Accordingly, Neo might have to make
multiple trips to the telephone booth to transport all the hostages. The hostages, will
continue to incur damage with every passing time step. A time step is the duration
taken by Neo to complete one action. With every time step, the damage of any hostage
increases by 2. If the damage of a hostage reaches 100, the hostage dies. There are
two cases to a hostage dying:
• If a hostage was never carried by Neo dies, this hostage turns into an agent and
then Neo must kill this agent.
• If a hostage is being carried by Neo dies, this hostage will not turn into an agent
and Neo will keep carrying its dead body.
Every time Neo performs a kill action (regardless of the number of agents killed by this
action), Neo’s damage increases by 20. Every time Neo takes a pill the damage of Neo
and all living hostages decreases by 20. Note that in this case where the action is taking
a pill, the damage of living hostages will not increase by 2 then decrease by 20 leading
in a decrease of 18, it will be simply a decrease of 20. Note that damage should not get
below zero. If Neo’s damage reaches 100 he dies and the game is over.
Using search you should formulate a plan that Neo can follow to complete the mission.
An optimal plan is one where the deaths (of hostages) are at a minimum as a first
condition. Given two plans with the same number of deaths, the more optimal plan
is the one where the total number of agents killed is minimal. The following search
strategies will be implemented and each will be used to help Neo:
a) Breadth-first search.
b) Depth-first search.
c) Iterative deepening search.
d) Uniform-cost search.
2
e) Greedy search with at least two heuristics.
f) A∗
search with at least two admissible heuristics.
