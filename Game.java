import java.util.Scanner;
import java.util.Arrays;


class Game {
  
  private enum state{
    Player1,
    Player2,
    Ongoing,
    Tie
  }

  private enum move{
    Player1,
    Player2
  }
  private enum minimaxTurn{
    Max,
    Min
  }
  private static int[][] board;

  // Togglable Settings
  public static int boardSizeX = 7;
  public static int boardSizeY = 6;
  public static int refreshRate = 75;
  public static boolean animationsOn = false;
  public static int minimaxDepth = 5;

  public static void main(String[] args) {
    Scanner scan = new Scanner(System.in);
    
    while (true){
      System.out.println("Welcome to CONNECT 4. ");
      System.out.println("Choose:  ");
      System.out.println("(1) Computer  ");
      System.out.println("(2) Player  ");
      System.out.println("(3) Toggle Settings  ");
      System.out.println("(-1) Quit  ");
      System.out.print("Enter choice: ");
      int choice = scan.nextInt();

      switch(choice){
        case 1: PVC(scan); clearScreen(); break;
        case 2: PVP(scan); clearScreen(); break;
        case 3: toggleSettings(scan); break;
        case -1: scan.close(); return;
        default: break;
      }
    }
    
    
}
  public static void toggleSettings(Scanner scan){
    int choice;
    boolean cont = true;
    while(cont){
      clearScreen();
      System.out.println("Settings: ");
      System.out.println("(1) Board Rows: "+boardSizeY);
      System.out.println("(2) Board Columns: "+boardSizeX);
      System.out.println("(3) Millisecond Refresh: "+refreshRate);
      System.out.println("(4) Animations on: "+animationsOn);
      System.out.println("(5) AI Search Depth: "+ minimaxDepth);
      System.out.println("(-1) Quit");
      System.out.print("Enter choice: ");
      choice = scan.nextInt();

      switch (choice){
        case -1: clearScreen(); cont=false; break;
        case 1: 
          System.out.print("Enter updated rows: ");
          boardSizeY = scan.nextInt();
          break;
        case 2:
          System.out.print("Enter updated columns: ");
          boardSizeX = scan.nextInt();
          break;
        case 3:
          System.out.print("Enter updated refresh rate (preferably under 150): ");
          refreshRate = scan.nextInt();
          break;
        case 4:
          System.out.print("Animations on?: ");
          animationsOn = scan.nextBoolean();
          break;
        case 5:
          System.out.print("New depth: ");
          minimaxDepth = scan.nextInt();
          break;
        default: break;
      }
    }
  }
  
  public static void PVP(Scanner scan){
    setBoard(boardSizeY,boardSizeX);
    int moveC;
    state status = state.Ongoing;
    displayBoard();
    while(true){

      do {
      System.out.print("Player 1 row: ");
      moveC = scan.nextInt();
      } while (!checkValid(moveC));
      
      clearScreen();
      if (animationsOn){animate(moveC,move.Player1);}
      place(moveC,move.Player1);
      displayBoard();

      if(checkWinner() == state.Player1){
        status = state.Player1;
        break;
      } else if (checkWinner() == state.Tie){
        status = state.Tie;
        break;
      }
      do {
      System.out.print("Player 2 row: ");
      moveC = scan.nextInt();
      } while(!checkValid(moveC));
      

      clearScreen();
      if (animationsOn){animate(moveC,move.Player2);}
      place(moveC,move.Player2); 
      displayBoard();

      if(checkWinner() == state.Player2){
        status = state.Player2;
        break;
      } else if (checkWinner() == state.Tie){
        status = state.Tie;
        break;
      }
    }

    switch(status){
      case Tie: System.out.println("Tie. ");  break;
      case Player1: System.out.println("Player 1 wins! "); break;
      case Player2: System.out.println("Player 2 wins! "); break;
      default: break;
    }
    try {
      Thread.sleep(1000);
    } catch(InterruptedException e){}
  }

  public static void PVC(Scanner scan){
    setBoard(boardSizeY,boardSizeX);
    int moveC;
    int[][] copyBoard = new int[board.length][board[0].length];
    state status = state.Ongoing;
    displayBoard();
    while(true){

      do {
      System.out.print("Player 1 row: ");
      moveC = scan.nextInt();
      } while (!checkValid(moveC));
      clearScreen();
      if (animationsOn){animate(moveC,move.Player1);}
      place(moveC,move.Player1);
      displayBoard();

      if(checkWinner() == state.Player1){
        status = state.Player1;
        break;
      } else if (checkWinner() == state.Tie){
        status = state.Tie;
        break;
      }

      try {
        Thread.sleep(750);
      } catch(InterruptedException e){}


      for(int i = 0; i < board.length; i++){
        copyBoard[i] = board[i].clone();
      }

      moveC = minimax(0,copyBoard,minimaxTurn.Min);
      clearScreen();
      if (animationsOn){animate(moveC,move.Player2);}
      place(moveC,move.Player2); 
      displayBoard();

      if(checkWinner() == state.Player2){
        status = state.Player2;
        break;
      } else if (checkWinner() == state.Tie){
        status = state.Tie;
        break;
      }
    }

    switch(status){
      case Tie: System.out.println("Tie. gg ");  break;
      case Player1: System.out.println("Player 1 wins! gg "); break;
      case Player2: System.out.println("Player 2 wins! gg ez "); break;
      default: break;
    }
    scan.nextLine(); // consume junk
    while(true){
      System.out.print("Quit? Y/N. ");

      switch (scan.nextLine().toUpperCase()){
        case "Y": return;
        case "N": System.out.println("Ok, I'll stay on this tab. "); break;
        default: System.out.println("Ok, I'll stay on this tab. "); break;
      }
    }

    
  }
    
  private static boolean checkValid(int column){
    // If the column exists
    if (column>=board.length){
      return false;
    }

    // If you can place something in the column
    for (int i=0; i<board[column].length; i++){
      if (board[column][i]==0){
        return true;
      }
    }
    return false;
  }
  
  private static int[][] place(int column, move curPlayer ){

    // Going from top of column down.
    for (int i=0; i<board[column].length; i++){

      // Run until you hit a piece. If you do, change the thing
      // on top of a piece to a number according to the curPlayer
      if (board[column][i]!=0){
        if (curPlayer==move.Player2){board[column][i-1]=2;}
        else{board[column][i-1]=1;}
        return board;
      } 
    }

    // Big wall of text. Basically, set the last piece to a num
    // based on the curPlayer.
    if (curPlayer==move.Player2){board[column][board[column].length-1]=2;}
    else{board[column][board[column].length-1]=1;}
    

    return board;
  }
  
  private static void setBoard (int rows, int columns){
    // The board is column row oriented, instead of row column.
    board = new int[columns][rows];
  }
  
  private static void displayBoard(){
    // The 2D arrays rows are actually its columns
    // Thus, it is kinda awkward to print out
    for (int i=0; i<board.length; i++){
      System.out.print("\u001B[36m"+i+" ");
    }
    System.out.println("\u001B[37m");
    int columnIndex = 0;
    int boardIndex = 0;
    while(columnIndex<board[0].length){

      if (board[boardIndex][columnIndex]==1){
        System.out.print("\u001B[31m"+board[boardIndex][columnIndex]+" "+"\u001B[37m");
      } else if (board[boardIndex][columnIndex]==2){
        System.out.print("\u001B[34m"+board[boardIndex][columnIndex]+" "+"\u001B[37m");
      } else{
         System.out.print(board[boardIndex][columnIndex]+" ");
      }
      boardIndex++;
      if (boardIndex==board.length){
        boardIndex=0;
        columnIndex++;
        System.out.println();
      }
    }
    System.out.println();
    

    
  }

  private static state checkWinner(){
    state vert = checkVert();
    state horiz = checkHoriz();
    state diag = checkDiag();

    if (vert==state.Player1 || horiz==state.Player1 || diag==state.Player1){
      return state.Player1;
    } else if (vert==state.Player2 || horiz==state.Player2 || diag==state.Player2){
      return state.Player2;
    }

    if (isFull()){return state.Tie;} 
    return state.Ongoing;
}
    
  private static state checkVert(){
    int count1,count2;
    int curPiece;
    count1=count2=0;

    for (int i=0; i<board.length; i++){
      count1=count2=0;
      for (int j=0; j<board[0].length; j++){
        curPiece = board[i][j];

        switch (curPiece){
          case 0: count1=count2=0; break;
          case 1: count1++; count2=0; break;
          case 2: count1=0; count2++; break;
        }

        if (count1==4){
          return state.Player1;
        } else if (count2==4){
          return state.Player2;
        }
        
      }
      
    }

    return state.Ongoing;
  }

  private static state checkHoriz(){
    int columnIndex,boardIndex,count1,count2,curPiece;
    columnIndex=boardIndex=count1=count2=0;
    
    while(columnIndex<board[0].length){
      curPiece = board[boardIndex][columnIndex];
      switch (curPiece){
          case 0: count1=count2=0; break;
          case 1: count1++; count2=0; break;
          case 2: count1=0; count2++; break;
      }
      if (count1==4){
        return state.Player1;
      } else if (count2==4){
        return state.Player2;
      }
      
      boardIndex++;
      if (boardIndex==board.length){
        count1 = count2 = 0;
        boardIndex=0;
        columnIndex++;
      }
    }
    return state.Ongoing;
  }

  private static int[][] obtainCoords(){
    int count = 0;
    int countCoord =0;
    for (int i=0; i<board.length; i++){

      for (int j=0; j<board[0].length; j++){

        if (board[i][j] !=0){
          count++;
        }
      }
    }
    int[][] coords = new int[count][2];
    for (int i=0; i<board.length; i++){

      for (int j=0; j<board[0].length; j++){

        if (board[i][j] !=0){
          coords[countCoord][0] = j;
          coords[countCoord][1] = i;
          countCoord++;
        }
      }
    }
    return coords;
  }
  
  private static state checkDiag(){
    int[][] coords = obtainCoords();
    int x,y;
    for (int i=0; i<coords.length; i++){
      x = coords[i][0];
      y = coords[i][1];
      state comp = helper(x,y);
      if (comp==state.Player1){
        return state.Player1;
      } else if (comp==state.Player2){
        return state.Player2;
      }
    }
    return state.Ongoing;
  }

  private static state checkDiagLeft(int x, int y){
    // -x +y
    int tempX, tempY;
    tempX = x;
    tempY = y;
    boolean win = true;
    for (int i=0; i<4; i++){
      if(!inBounds(x,y)){
        win = false;
        break;
      }
      if(board[y][x]!=board[tempY][tempX]){
        win = false;
        break;
      }
      x-=1;
      y+=1;
    }
    if (win){
      if (board[tempY][tempX]==1){
        return state.Player1;
      }
      return state.Player2;
    }

    return state.Ongoing;
  }
  
  private static state checkDiagRight(int x, int y){
    // +x +y
    int tempX, tempY;
    tempX = x;
    tempY = y;
    boolean win = true;
    for (int i=0; i<4; i++){
      if(!inBounds(x,y)){
        win = false;
        break;
      }
      if(board[y][x]!=board[tempY][tempX]){
        win = false;
        break;
      }
      x+=1;
      y+=1;
    }
    if (win){
      if (board[tempY][tempX]==1){
        return state.Player1;
      }
      return state.Player2;
    }

    return state.Ongoing;
  }
  
  private static boolean inBounds(int x, int y){
    if (y < 0 || y >= board.length) {
      return false;
    } else if (x < 0 || x >= board[0].length) {
      return false;
    }
    return true;
  }
  
  private static boolean isFull(){
    for (int i=0; i<board.length; i++){

      for (int j=0; j<board[0].length; j++){
        if(board[i][j]==0){
          return false;
        }
      }
    }
    return true;
  }

  private static state helper(int x, int y){
    if (checkDiagLeft(x,y)==state.Player1 || checkDiagRight(x,y)==state.Player1){
      return state.Player1;
    } else if (checkDiagLeft(x,y)==state.Player2 || checkDiagRight(x,y)==state.Player2){
      return state.Player2;
    } else{
      return state.Ongoing;
    }
  }

  private static void clearScreen(){
    System.out.print("\033[H\033[2J");
    System.out.flush();
  }

  private static void animate(int column, move player){
    int symbol;
    int curVal;
    switch(player){
      case Player1: symbol=1; break;
      case Player2: symbol=2; break;
      default: symbol=-1; break;
    }
    for (int i=0; i<board[column].length; i++){
      curVal = board[column][i];

      if (curVal!=0){
        break;
      }

      board[column][i]=symbol;
      displayBoard();
      try {
        Thread.sleep(refreshRate);
      } catch(InterruptedException e){}
      clearScreen();
      board[column][i]=0;
    }
    
  }
  
  private static int[] findValidMoves (int[][] simBoard){

    int count = 0;
    for (int j=0; j<simBoard.length; j++){
      for (int i=0; i<simBoard[j].length; i++){
        if (simBoard[j][i]==0){
          count++;
          break;
        }
      }
    }
    int[] res = new int[count];
    count = 0;
    for (int j=0; j<simBoard.length; j++){
      for (int i=0; i<simBoard[j].length; i++){
        if (simBoard[j][i]==0){
          res[count] = j;
          count++;
          break;
        }
      }
    }
    
    return res;
  }


  // Heuristic Evaluator
  private static int clumpScore(int[][] simBoard){
    int playerScore3 = vertClumpScore(simBoard, 1,3)+ horizClumpScore(simBoard,1,3);
    int compScore3 = -vertClumpScore(simBoard, 2,3) - horizClumpScore(simBoard,2,3) ;

    int playerScore2 = vertClumpScore(simBoard, 1,2)+ horizClumpScore(simBoard,1,2);
    int compScore2 = -vertClumpScore(simBoard, 2,2) - horizClumpScore(simBoard,2,2) ;
    
    return 2*(playerScore3+compScore3) + (playerScore2+compScore2);
  }

  private static int horizClumpScore(int[][] simBoard, int code, int terminal){
    int columnIndex = 0;
    int boardIndex = 0;
    int clumpCount = 0;
    int count = 0;

    while (columnIndex < simBoard[0].length){
      if (count==terminal){
        clumpCount++;
        count = 0;
        continue;
      }

      if (simBoard[boardIndex][columnIndex]==0){
        count++;
      }

      boardIndex++;
      if (boardIndex==board.length){
        boardIndex=0;
        columnIndex++;
      }
      
    }
    return clumpCount;
  }
  private static int vertClumpScore(int[][] simBoard,int code, int terminal){
    int clumpCount, count;
    clumpCount = count = 0;

    for (int i=0; i<simBoard.length; i++){
      for (int j=0; j<simBoard[0].length; j++){
        if (count==terminal){
         clumpCount++; 
          count = 0;
          continue;
        }
        if (simBoard[i][j]==code){
          count++;
        } else {
          count = 0;
        }
        
      }
    }

    return clumpCount;
  }

// Evaluate to a terminal state (winning) or evaluate to a certain depth and use a heuristic evaluator to find the "score"
  private static int minimax(int depth, int[][] simBoard, minimaxTurn turn){
    // PC min, player max.
    // Player is P1, PC is P2
    switch (checkWinner(simBoard)){
      case Player1: return 10000/depth; 
      case Player2: return -10000/depth;
      case Tie: return 0;
      default: break;
    }
    int[] validMoves = findValidMoves(simBoard);
    int[] valueArr = new int[validMoves.length];
    int[][] recSimBoard;
    int[][] copyBoard = new int[simBoard.length][simBoard[0].length];
    int eval;

    
    if (depth==minimaxDepth){
      return clumpScore(simBoard);
    } else if (depth==0){
      for (int i=0; i<validMoves.length; i++){
        for(int j = 0; j < simBoard.length; j++){
          copyBoard[j] = simBoard[j].clone();
        }
        recSimBoard = simPlace(validMoves[i],minimaxTurn.Min,copyBoard);
        eval = minimax(depth+1,recSimBoard,minimaxTurn.Max); 
        valueArr[i] = eval;
      }
      System.out.println("AI's values are: ");
      printArray(valueArr);
      return validMoves[findMinIndex(valueArr)];
    } else {     
      
      switch (turn){
        case Max:
          for (int i=0; i<validMoves.length; i++){
            for(int j = 0; j < simBoard.length; j++){
              copyBoard[j] = simBoard[j].clone();
            }
            recSimBoard = simPlace(validMoves[i],minimaxTurn.Max,copyBoard);
            eval = minimax(depth+1,recSimBoard,minimaxTurn.Min);
            valueArr[i] = eval;
            
          }
          return findMax(valueArr);
        case Min:
          for (int i=0; i<validMoves.length; i++){
            for(int j = 0; j < simBoard.length; j++){
              copyBoard[j] = simBoard[j].clone();
            }
            recSimBoard = simPlace(validMoves[i],minimaxTurn.Min,copyBoard);
            eval = minimax(depth+1,recSimBoard,minimaxTurn.Max);
            valueArr[i] = eval;
          }
          return findMin(valueArr);
        default: System.out.println("An error occured. "); return 500;
          
      }
    }
  }

  private static int[][] simPlace(int column, minimaxTurn curPlayer, int[][] simBoard ){

    // Going from top of column down.
    for (int i=0; i<simBoard[column].length; i++){

      // Run until you hit a piece. If you do, change the thing
      // on top of a piece to a number according to the curPlayer
      if (simBoard[column][i]!=0){
        if (curPlayer==minimaxTurn.Min){simBoard[column][i-1]=2;}
        else{simBoard[column][i-1]=1;}
        return simBoard;
      } 
    }

    // Big wall of text. Basically, set the last piece to a num
    // based on the curPlayer.
    if (curPlayer==minimaxTurn.Min){simBoard[column][simBoard[column].length-1]=2;}
    else{simBoard[column][simBoard[column].length-1]=1;}
    

    return simBoard;
  }

  private static int findMaxIndex(int[] arr){
    int curMax = -10000;
    int maxIndex = 0;
    for (int i=0; i<arr.length; i++){
      if (arr[i]>curMax){
        curMax = arr[i];
        maxIndex = i;
      }
    }
    return maxIndex;
  }

  private static int findMinIndex(int[] arr){
    int curMin = 10000;
    int minIndex = 0;
    for (int i=0; i<arr.length; i++){
      if (arr[i]<curMin){
        curMin = arr[i];
        minIndex = i;
      }
    }
    return minIndex;
  }
  
  private static int findMax(int[] arr){
    int curMax = -10000;
    for (int i=0; i<arr.length; i++){
      if (arr[i]>curMax){
        curMax = arr[i];
      }
    }
    return curMax;
  }

  private static int findMin(int[] arr){
    int curMin = 10000;
    for (int i=0; i<arr.length; i++){
      if (arr[i]<curMin){
        curMin = arr[i];
      }
    }
    return curMin;
  }

// Minimax winning checker. Different only that it accepts a board as an arg, instead of just using
// the board attribute.
  private static state checkWinner(int[][] simBoard){
    state vert = checkVert(simBoard);
    state horiz = checkHoriz(simBoard);
    state diag = checkDiag(simBoard);

    if (vert==state.Player1 || horiz==state.Player1 || diag==state.Player1){
      return state.Player1;
    } else if (vert==state.Player2 || horiz==state.Player2 || diag==state.Player2){
      return state.Player2;
    }

    if (isFull(simBoard)){return state.Tie;} 
    return state.Ongoing;
}
    
  private static state checkVert(int[][] simBoard){
    int count1,count2;
    int curPiece;
    count1=count2=0;

    for (int i=0; i<simBoard.length; i++){
      count1=count2=0;
      for (int j=0; j<simBoard[0].length; j++){
        curPiece = simBoard[i][j];

        switch (curPiece){
          case 0: count1=count2=0; break;
          case 1: count1++; count2=0; break;
          case 2: count1=0; count2++; break;
        }

        if (count1==4){
          return state.Player1;
        } else if (count2==4){
          return state.Player2;
        }
        
      }
      
    }

    return state.Ongoing;
  }

  private static state checkHoriz(int[][] simBoard){
    int columnIndex,boardIndex,count1,count2,curPiece;
    columnIndex=boardIndex=count1=count2=0;
    
    while(columnIndex<simBoard[0].length){
      curPiece = simBoard[boardIndex][columnIndex];
      switch (curPiece){
          case 0: count1=count2=0; break;
          case 1: count1++; count2=0; break;
          case 2: count1=0; count2++; break;
      }
      if (count1==4){
        return state.Player1;
      } else if (count2==4){
        return state.Player2;
      }
      
      boardIndex++;
      if (boardIndex==simBoard.length){
        count1=count2=0;
        boardIndex=0;
        columnIndex++;
      }
    }
    return state.Ongoing;
  }

  private static int[][] obtainCoords(int[][] simBoard){
    int count = 0;
    int countCoord =0;
    for (int i=0; i<simBoard.length; i++){

      for (int j=0; j<simBoard[0].length; j++){

        if (simBoard[i][j] !=0){
          count++;
        }
      }
    }
    int[][] coords = new int[count][2];
    for (int i=0; i<simBoard.length; i++){

      for (int j=0; j<simBoard[0].length; j++){

        if (simBoard[i][j] !=0){
          coords[countCoord][0] = j;
          coords[countCoord][1] = i;
          countCoord++;
        }
      }
    }
    return coords;
  }
  
  private static state checkDiag(int[][] simBoard){
    int[][] coords = obtainCoords(simBoard);
    int x,y;
    for (int i=0; i<coords.length; i++){
      x = coords[i][0];
      y = coords[i][1];
      state comp = helper(simBoard,x,y);
      if (comp==state.Player1){
        return state.Player1;
      } else if (comp==state.Player2){
        return state.Player2;
      }
    }
    return state.Ongoing;
  }

  private static state checkDiagLeft(int[][] simBoard, int x, int y){
    // -x +y
    int tempX, tempY;
    tempX = x;
    tempY = y;
    boolean win = true;
    for (int i=0; i<4; i++){
      if(!inBounds(simBoard,x,y)){
        win = false;
        break;
      }
      if(simBoard[y][x]!=simBoard[tempY][tempX]){
        win = false;
        break;
      }
      x-=1;
      y+=1;
    }
    if (win){
      if (simBoard[tempY][tempX]==1){
        return state.Player1;
      }
      return state.Player2;
    }

    return state.Ongoing;
  }
  
  private static state checkDiagRight(int[][] simBoard, int x, int y){
    // +x +y
    int tempX, tempY;
    tempX = x;
    tempY = y;
    boolean win = true;
    for (int i=0; i<4; i++){
      if(!inBounds(simBoard,x,y)){
        win = false;
        break;
      }
      if(simBoard[y][x]!=simBoard[tempY][tempX]){
        win = false;
        break;
      }
      x+=1;
      y+=1;
    }
    if (win){
      if (simBoard[tempY][tempX]==1){
        return state.Player1;
      }
      return state.Player2;
    }

    return state.Ongoing;
  }
  
  private static boolean inBounds(int[][] simBoard, int x, int y){
    if (y < 0 || y >= simBoard.length) {
      return false;
    } else if (x < 0 || x >= simBoard[0].length) {
      return false;
    }
    return true;
  }
  
  private static boolean isFull(int[][] simBoard){
    for (int i=0; i<simBoard.length; i++){

      for (int j=0; j<simBoard[0].length; j++){
        if(simBoard[i][j]==0){
          return false;
        }
      }
    }
    return true;
  }

  private static state helper(int[][] simBoard, int x, int y){
    if (checkDiagLeft(simBoard,x,y)==state.Player1 || checkDiagRight(simBoard,x,y)==state.Player1){
      return state.Player1;
    } else if (checkDiagLeft(simBoard,x,y)==state.Player2 || checkDiagRight(simBoard,x,y)==state.Player2){
      return state.Player2;
    } else{
      return state.Ongoing;
    }
  }


  private static void printArray(int[] arr){
    for (int i=0; i<arr.length;i++){
      System.out.print(arr[i]+" ");
    }
    System.out.println();
  }

}
