package com.goldthump.chess;

		//create the e-numb-data-type for player
		enum Player
		{
				WHITE,
				BLACK,
		}
		
		//create the e-numb-data-type for Rank
		enum Rank
		{
			KING,
			QUEEN,
			BISHOP,
			ROOK,
			KNIGHT,
			PAWN,
		}
		
		//create the chess piece class here to set the layer by rank wise and player
	public class ChessPiece 
	{
			private final int col;
			private final int row;
			private final Player player;
			private final Rank rank;
			private final String imgName;
			
			//create the constructors for all local variables
			public ChessPiece(int col, int row, Player player, Rank rank, String imgName)
				{
				super();
				this.col = col;
				this.row = row;
				this.player = player;
				this.rank = rank;
				this.imgName = imgName;
				}
			
			//create the getter methods for local variables
			public int getCol() 
			{
				return col;
			}
			public int getRow()
			{
				return row;
			}
			public Player getPlayer() 
			{
				return player;
			}
			public Rank getRank()
			{
				return rank;
			}
			public String getImgName() 
			{
				return imgName;
			}
	}