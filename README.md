# Pokemon Battle simulation

The project will involve the implementation of an AI capable of winning in the battle system of the first-generation Pokemon game. The Pokemon battle system consists of two opponents, each with the same number of Pokemon (fighting characters), which in our application will be three. The goal of the battle is to defeat all of the opponent's Pokemon. The battle is turn-based, in which you can choose a move for your Pokémon to use, or you have the option to switch to another Pokémon that is still alive.
Each Pokemon has specific and distinct characteristics from the others. Each Pokemon has four statistics: Attack, Defense, Special, and Speed, they have four possible moves and they can also be of one or two types.

Types: Pokemon can be of one or two types, and in the first generation, there are a total of 15 types. Each type is weak or strong against other types, depending on the type of Pokemon they are facing. When one type is strong against another, the damage dealt to the other Pokémon is doubled, and in case it's weak, the damage is halved. There's also the possibility that one type has no effect on another.

![types (2)](https://github.com/user-attachments/assets/575c8c68-7553-4f4b-932c-52a3f4e91892)

Moves: Each Pokemon has a total of four moves, and these moves are of a specific type, for example, fire type. These move types are independent of the Pokemon's own type, meaning a water-type Pokemon could learn a fire-type move, although it's not common. Moves can be physical, special or Status(not implemented). Each move is associated with a power and an accuracy. The higher the power, the more damage it deals, and accuracy determines how likely the attack is to hit. These statistics can be displayed during the battle.

## Statistics:

Attack (Atk): Represents the strength of the Pokemon's physical moves.
Defense (Def): Represents the Pokemon's ability to resist damage from the opponent's physical moves.
Speed (Speed): Determines how quickly a Pokemon acts in battle. If a Pokemon has higher speed than the opponent, it will attack first, and if they have the same speed, it's resolved with a 50/50 chance.
Special Attack (SpA): the power of special moves .
Special Defend(SpD): the resistance to the opponent's special moves.
Hit Points (HP): Represents the amount of damage a Pokemon can take before fainting, i.e., being defeated.

These statistics can be displayed during the battle, but only for the player's own Pokémon, and to give an advantage to inexperienced players, a range of possible speeds for the opponent's Pokémon will be provided to guide the user.

Knowing all the above, the damage caused when performing an attack on a Pokémon is calculated as follows:

![images (6)](https://github.com/user-attachments/assets/7cb11842-e7a1-423e-9290-1399b0ef248a)


Level: Same level for all Pokémon.

Attack: The attack stat of the Pokémon using the move.

Defense: The Defense stat of the enemy Pokémon.

Base: The power of the move used.

STAB:  By default it is 1, but If the move used is the same type of the Pokémon, this value is 1.5.

Type: If the move type is strong versus the type of the opponent Pokémon this value is 2, if the move type is weak versus the type of the opponent Pokémon this value is 0.5 and if the move type is No effect versus the type of the opponent Pokémon this value is 0.

Critical: By default it is 1, but there is a 6.25% chance of a critical hit that would do double damage, in this case, the value of Critical is 2.

Other: This value will not be used.

## Set of Pokémons:



![Captura de pantalla 2023-11-16 165713](https://github.com/user-attachments/assets/cdcc546b-1fb1-4212-a947-a25ec64a424d)![Captura de pantalla 2023-11-16 165720](https://github.com/user-attachments/assets/88fb3d11-c508-4f3b-8c77-d15c05abdf86)![Captura de pantalla 2023-11-16 165726](https://github.com/user-attachments/assets/ededbd6a-0a7d-4216-8312-5dc004527f78)![Captura de pantalla 2023-11-16 165753](https://github.com/user-attachments/assets/f23aae42-b99e-44c6-bb62-6741d9a37630)
![Captura de pantalla 2023-11-16 165859](https://github.com/user-attachments/assets/2704c8bf-df53-4d97-af23-693f4657be4f)![Captura de pantalla 2023-11-16 165237](https://github.com/user-attachments/assets/089ad2c6-5ce7-4a42-839a-a51b0a7f6913)![Captura de pantalla 2023-11-16 165257](https://github.com/user-attachments/assets/ecdf2378-7e3e-4605-9a4a-90dd49cacf16)![Captura de pantalla 2023-11-16 165410 (1)](https://github.com/user-attachments/assets/b1a6fb97-22d7-4097-982e-4cc7114a8b79)![Captura de pantalla 2023-11-16 165432 (2)](https://github.com/user-attachments/assets/dd99f05e-2e43-4317-98e3-69d2ed127449)![Captura de pantalla 2023-11-16 165528 (1)](https://github.com/user-attachments/assets/f6409b98-3452-486b-9f08-74d1590aca28)


## Set of moves:




![1_p (1)](https://github.com/user-attachments/assets/a3daa3c1-b25a-4417-868b-102d0adefd47)![2_p](https://github.com/user-attachments/assets/46fa2506-b3d3-4907-acc0-2077813aa8f5)![3_p](https://github.com/user-attachments/assets/f4bad615-a5fe-48ee-b8dc-8dcaa5f788b8)![4_p](https://github.com/user-attachments/assets/4f00256c-9cde-45fe-86c0-830bbdf67870)![5_p](https://github.com/user-attachments/assets/0ab63f0e-e877-4d71-9477-0e10ee58914d)![6_p](https://github.com/user-attachments/assets/fdd884b6-c37f-4d10-bda8-e67f87f1a086)![6_p](https://github.com/user-attachments/assets/286f5e5a-2a2f-45bc-91d0-a7254aa4c4b3)![7_p](https://github.com/user-attachments/assets/9dfaa480-eb37-4c03-961f-cea272f416c7)

![8_p](https://github.com/user-attachments/assets/d990df81-3c4e-4939-8954-270b4fe4c9e1)



## Chosing Monte Carlo Tree Search 

The choice of Monte Carlo Tree Search (MCTS) as the foundation for the artificial intelligence in our Pokémon game simulator has been based on the evaluation of various strategies. Unlike other methods, such as Q-learning and neural networks, which, according to previous studies, yield mediocre results even with intensive training, MCTS emerges as a more robust option.The adaptability of MCTS stands out in the context of Pokémon. In a turn-based game, where information about the opponent's moves is not always known, MCTS effectively manages sequential decision-making, making it suitable for the dynamics of Pokémon battles.

In terms of Pokémon battles, characterized by the complexity arising from various species, moves, and strategies, MCTS addresses this intricate dynamic by exploring practically all possibilities, depending on the depth of exploration allowed. This approach enables it to focus on the most promising options through repeated simulations, overcoming the inherent complexity of the game.It is crucial to highlight that MCTS does not require intensive pre-training, unlike other strategies. Its ability to practically consider all possibilities, adjusting the depth of exploration, positions it as an approach that efficiently adapts to the changing complexities of the game.


## Views




