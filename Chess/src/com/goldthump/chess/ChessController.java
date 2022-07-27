package com.goldthump.chess;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.Executors;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class ChessController implements ChessDelegate,ActionListener

{
	private String SOCKET_SERVER_ADDR = "localhost";
 	private int PORT =50000;
	private ChessModel chessModel = new ChessModel();
	private JFrame frame;
	private ChessView chessBoardPanel;
	private JButton resetBtn;
	private JButton serverBtn;
	private JButton clientBtn;
	private PrintWriter printWriter;
	private Socket socket;
	private ServerSocket listener;
	
	 ChessController() 
	 {
		 		//reset method has called from chess model class
		 		chessModel.reset();
		 		
		 		//details of frame developing
		 		frame = new JFrame("Chess");
		 		frame.setSize(500, 550);
		 		frame.setLocation(200, 1300);
		 		frame.setLayout(new BorderLayout());
		 		
		 		//create the chess board panel
		 		chessBoardPanel = new ChessView(this);
		 		frame.add(chessBoardPanel, BorderLayout.CENTER);
		 		var buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		 		
		 		//create the reset button
		 		resetBtn = new JButton("Reset");
		 		resetBtn.addActionListener(this);
		 		buttonsPanel.add(resetBtn);
		 		
		 		//create the server button
				serverBtn = new JButton("listen");
				buttonsPanel.add(serverBtn);
				serverBtn.addActionListener(this);
				
				//create the client button
				clientBtn = new JButton("Connect");
				buttonsPanel.add(clientBtn);
				clientBtn.addActionListener(this);
				
				//chess board visible command
				frame.add(buttonsPanel, BorderLayout.PAGE_END);
				frame.setVisible(true);
				
				//when you stop the or click right corner of the cross simple ,the server will be automatically stop
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				
				
				frame.addWindowListener(new WindowAdapter()
				{
					@Override
					public void windowClosing(WindowEvent e)
					{
						super.windowClosing(e);
						if(printWriter != null)printWriter.close();
						try 
						{
							if(listener != null)listener.close();
							if(socket !=  null)socket.close();
						} 
						catch (IOException e1) 
						{
							e1.printStackTrace();
						}
					}
	
				});
	 } 
	 
	//main class
	public static void main(String[] args)
	{
		new ChessController();
	}

	@Override
	public ChessPiece pieceAt(int col, int row) 
	{
		return chessModel.pieceAt(col, row);
	}

	//movePiece method has called fromchessModel
	@Override
	public void movePiece(int fromCol, int fromRow, int toCol, int toRow)
	{
		chessModel.movePiece(fromCol, fromRow, toCol, toRow);
		chessBoardPanel.repaint();
		if(printWriter !=null)
		{
		printWriter.println(fromCol + ","+fromRow + ","+ toCol + ","+toRow);
		}
		
	}
	
	//scanning the moving pieces
	private void receiveMove(Scanner scanner) 
	{
		
		while(scanner.hasNextLine()) 
		{
			var moveStr = scanner.nextLine();
			System.out.println("chess move received" + moveStr);
			var moveStrArr = moveStr.split(",");
			var fromCol = Integer.parseInt(moveStrArr[0]);
			var fromRow = Integer.parseInt(moveStrArr[1]);
			var toCol = Integer.parseInt(moveStrArr[2]);
			var toRow = Integer.parseInt(moveStrArr[3]);
			SwingUtilities.invokeLater(new Runnable() 
			{
				@Override
				public void run() 
				{
					chessModel.movePiece(fromCol, fromRow, toCol, toRow);
					chessBoardPanel.repaint();
				}
			});
		}
	}
	
	//to run the socket server
	public void runSocketServer() 
	{
		Executors.newFixedThreadPool(1).execute(new Runnable()
		{
			@Override
			public void run() 
			{
				try 
				{
					listener = new ServerSocket(PORT);
					System.out.println("server is listening on port" +PORT);
					socket = listener.accept();
					printWriter =new PrintWriter(socket.getOutputStream(),true);
					var scanner = new Scanner(socket.getInputStream());
					receiveMove(scanner);
					}
				catch (Exception e1) 
				{
					e1.printStackTrace();
				 }
			}
		});
	}
	
	//to run the socket client
	private void runSocketClient() 
	{
			try 
			{
				socket = new Socket(SOCKET_SERVER_ADDR,PORT);
				System.out.println("client connected to port" + PORT);
				var scanner = new Scanner(socket.getInputStream());
				printWriter = new PrintWriter(socket.getOutputStream(),true);
				
				//execute the moving places
				Executors.newFixedThreadPool(1).execute(new Runnable() 
				{
					@Override
					public void run() 
					{
						receiveMove(scanner);
					}
				});
				
			}  
			catch (IOException e1) 
			{
				e1.printStackTrace();
			}
	}
	@Override
	public void actionPerformed(ActionEvent e) 
	{
		System.out.println(e.getSource());
		if(e.getSource() == resetBtn)
		{
			chessModel.reset();
			chessBoardPanel.repaint();
			try 
			{
				if(listener != null) 
				{
				listener.close();
				}
				if(socket != null) 
				{
				socket.close();
				}
				serverBtn.setEnabled(true);
				clientBtn.setEnabled(true);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		else if(e.getSource() == serverBtn)
		{	
			serverBtn.setEnabled(false);
			clientBtn.setEnabled(false);
			frame.setTitle("chess server");
			runSocketServer();
			JOptionPane.showMessageDialog(frame, "Listening on PORT " + PORT);
		}	
		else if(e.getSource() == clientBtn)
		{
			serverBtn.setEnabled(false);
			clientBtn.setEnabled(false);
			frame.setTitle("chess client");
			runSocketClient();
			JOptionPane.showMessageDialog(frame, "connected to PORT " + PORT);
		}
	}
}
