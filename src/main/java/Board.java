package main.java;
import java.util.*;

import main.java.util.*;

public class Board {

    /**
     * Represents the 8x8 chess board. Each cell can contain a Piece or be null if there is no piece on that cell.
     */
    public Piece[][] BOARD;

    /**
     * A mapping from each Piece type to a list of its pseudo-legal displacements. 
     * Each move is represented as a displacement vector (dx, dy).
     */
    public EnumMap<Name, HashSet<Square>> PSEUDO_LEGAL_MOVEMENT;

    /**
     * The current state of the game. This can be ACTIVE, STALEMATE, BLACKWON, WHITEWON, or INSUFFICIENT_MATERIAL.
     */
    public GameState GAME_STATE;

    /**
     * The number of moves that have been made in the game so far.
     */
    public int MOVE_NUMBER;

    /**
     * A history of all moves made in the game.
     */
    public HashMap<Integer, Move> MOVE_HISTORY;    

    /**
     * A boolean that is true if it's white's turn to move and false if it's black's turn.
     */
    public Boolean WHITE_TURN;

    /**
     * An integer that represents the orientation of the board. 1 represents the normal orientation and -1 represents the flipped orientation.
     */
    public int BOARD_ORIENTATION;

    /**
     * Constructor
     */
    public Board(){

        this.PSEUDO_LEGAL_MOVEMENT = new EnumMap<>(Name.class);
        initPSEUDO_LEGAL_MOVEMENT();

        this.MOVE_HISTORY = new HashMap<>();

    }

    /**
     * Initializes PSEUDO_LEGAL_MOVEMENT with piece names and their displacements
     */
    private void initPSEUDO_LEGAL_MOVEMENT(){
        HashSet<Square> PAWNW = new HashSet<>(Arrays.asList(new Square(0,-1), new Square(-1,-1), new Square(1,-1)));
        
        HashSet<Square> PAWNB = new HashSet<>(Arrays.asList(new Square(0,1), new Square(-1,1), new Square(1,1)));

        HashSet<Square> KNIGHT = new HashSet<>(
        Arrays.asList(
            new Square(1,2), new Square(-1,2), new Square(1,-2), new Square(-1,-2),
            new Square(2,1), new Square(-2,1), new Square(2,-1), new Square(-2,-1)
        ));

        HashSet<Square> BISHOP = new HashSet<>(
        Arrays.asList(
            new Square(1,1), new Square(-1,1), new Square(1,-1), new Square(-1,-1)
        ));

        HashSet<Square> ROOK = new HashSet<>(
        Arrays.asList(
            new Square(1,0), new Square(-1,0), new Square(0,1), new Square(0,-1)
        ));

        HashSet<Square> QUEEN = new HashSet<>();
        QUEEN.addAll(BISHOP);
        QUEEN.addAll(ROOK);

        HashSet<Square> KING = new HashSet<>();
        KING.addAll(BISHOP);
        KING.addAll(ROOK);

        this.PSEUDO_LEGAL_MOVEMENT.put(Name.PAWNW,PAWNW);
        this.PSEUDO_LEGAL_MOVEMENT.put(Name.PAWNB,PAWNB);
        this.PSEUDO_LEGAL_MOVEMENT.put(Name.KNIGHT,KNIGHT);
        this.PSEUDO_LEGAL_MOVEMENT.put(Name.BISHOP,BISHOP);
        this.PSEUDO_LEGAL_MOVEMENT.put(Name.ROOK,ROOK);
        this.PSEUDO_LEGAL_MOVEMENT.put(Name.QUEEN,QUEEN);
        this.PSEUDO_LEGAL_MOVEMENT.put(Name.KING,KING);

    }

    public void setBoard(){ setBoard("start"); }

    /**
     * Creates a board with pieces according to this fenposition.
     * Updates GameState, Move_Number etc....
     * @param fenposition
     */
    public void setBoard(String fenposition){

        this.BOARD = new Piece [8][8];

        this.MOVE_NUMBER = 1;

        switch (fenposition){

            case "castle_test":
                fenposition = "r3k2r/pppppppp/8/8/8/8/PPPPPPPP/R3K2R w KQkq - 0 0";
                break;
            case "castle_white_king_side":
                fenposition = "4k3/8/8/8/8/8/8/4K2R w KQkq - 0 0";
                break;
            case "start":
                fenposition = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - random";
                break;
            case "random":
                fenposition = "r3k2r/ppqbpp1p/n1pp2pb/8/8/2PP1NP1/PPQ1PPBP/2KR3R b kq - random";
                break;
            case "knight_test":
                fenposition = "2k5/8/8/8/4N3/8/8/2K5 w - - 0 59";
                break;
            case "pawn_test":
                fenposition = "2k5/1P6/8/5pP1/8/3p4/4P3/2K5 w - f5 0 59";
                break;
            default:
                break;
        }

        String[] boardInformation = fenposition.split("\\s+");

        String position = boardInformation[0];
        String turn = boardInformation[1];
        String castleRights = boardInformation[2];
        boolean whiteKingCastle = (castleRights.indexOf('K') != -1) || (castleRights.indexOf('Q') != -1);
        boolean blackKingCastle = (castleRights.indexOf('k') != -1) || (castleRights.indexOf('q') != -1);

        String enpassantSquare = boardInformation[3];
        String halfmoves = boardInformation[4];

        // Set turn
        this.WHITE_TURN = turn.equals("w");

        int row = 0;
        int col = 0;

        // Set position
        for(int i=0;i<position.length();i++){
            char c = position.charAt(i);
            Name name = null;
            boolean white = true;
            Piece current;
            switch(c){
                case 'r':
                    name = Name.ROOK;
                    white = false;
                    break;
                case 'n':
                    name = Name.KNIGHT;
                    white = false;
                    break;
                case 'b':
                    name = Name.BISHOP;
                    white = false;
                    break;
                case 'q':
                    name = Name.QUEEN;
                    white = false;
                    break;
                case 'k':
                    name = Name.KING;
                    white = false;
                    break;
                case 'p':
                    name = Name.PAWNB;
                    white = false;
                    break;
                case 'R':
                    name = Name.ROOK;
                    white = true;
                    break;
                case 'N':
                    name = Name.KNIGHT;
                    white = true;
                    break;
                case 'B':
                    name = Name.BISHOP;
                    white = true;
                    break;
                case 'Q':
                    name = Name.QUEEN;
                    white = true;
                    break;
                case 'K':
                    name = Name.KING;
                    white = true;
                    break;
                case 'P':
                    name = Name.PAWNW;
                    white = true;
                    break;
                case '/':
                    name = null;
                    col = -1;
                    row+=1;
                    break;
                default:
                    name = null;
                    col += Integer.parseInt(c+"")-1;
                    break;
            }

            if (name != null){
                current = new Piece(name,white,col,row);
                setPiece(current,new Square(col,row));
                current.firstMove = (current.name == Name.PAWNW && row == 6) || (current.name == Name.PAWNB && row == 1) || (current.name == Name.KING && current.white && whiteKingCastle) || (current.name == Name.KING && !current.white && blackKingCastle);
            }

            

            col = (col >= 8) ? 0:col+1;

        }

        Piece whiteRookQueenSide =  (getPiece(new Square(0,7)) != null && getPiece(new Square(0,7)).name == Name.ROOK) ?  getPiece(new Square(0,7)) : null;
        Piece whiteRookKingSide =  (getPiece(new Square(7,7)) != null && getPiece(new Square(7,7)).name == Name.ROOK) ?  getPiece(new Square(7,7)) : null;
        Piece blackRookQueenSide =  (getPiece(new Square(0,0)) != null && getPiece(new Square(0,0)).name == Name.ROOK) ?  getPiece(new Square(0,0)) : null;
        Piece blackRookKingSide =  (getPiece(new Square(7,0)) != null && getPiece(new Square(7,0)).name == Name.ROOK) ?  getPiece(new Square(7,0)) : null;

        if (whiteRookKingSide != null) whiteRookKingSide.firstMove = false;
        if (whiteRookQueenSide != null) whiteRookQueenSide.firstMove = false;
        if (blackRookKingSide != null) blackRookKingSide.firstMove = false;
        if (blackRookQueenSide != null) blackRookQueenSide.firstMove = false;

        if (!castleRights.equals("-")){
            for(char c: castleRights.toCharArray()){
                if (c == '-') continue;

                if (c == 'K' && whiteRookKingSide != null){
                    whiteRookKingSide.firstMove = true;
                }
                if (c == 'Q' && whiteRookQueenSide != null){
                    whiteRookQueenSide.firstMove = true;
                }
                if (c == 'k' && blackRookKingSide != null){
                    blackRookKingSide.firstMove = true;
                }
                if (c == 'q' && blackRookQueenSide != null){
                    blackRookQueenSide.firstMove = true;
                } 

            }
        }

        if (!enpassantSquare.equals("-")){
            // Square conversion: ASCII code - 97
            row = 8 - Integer.parseInt(enpassantSquare.charAt(1)+"");
            col = enpassantSquare.charAt(0) - 97;
            Piece enPassantPiece = getPiece(new Square(col, row));
            if (enPassantPiece != null) enPassantPiece.enPassant = true;      
        }

        //printBoard(null);
    }

    public HashMap<Piece, HashSet<Move>> getCurrentLegalMoves(){
        HashMap<Piece, HashSet<Move>> CURRENT_LEGAL_MOVES = new HashMap<>();

        for(Piece piece: getPieces(this.WHITE_TURN)){
            HashSet<Move> moves = legalMoves(piece, true);
            CURRENT_LEGAL_MOVES.put(piece, moves);
        }


        return CURRENT_LEGAL_MOVES;

        

    }

    public String getPositionFenstring(){

        throw new UnsupportedOperationException();

    }

    public void printBoard(Move highlight){
        
        for(int row=0; row<8; row++){
            for(int col=0; col<8; col++){
                Piece piece = getPiece(new Square(col,row));
                if (piece != null) System.out.print(piece.getLetter());
                else System.out.print(". ");
            }
            System.out.println("\n");
        }

    }

    //TODO:
    // public void printCurrentMoves(){
    //     HashMap<Piece, HashSet<Move>> moves = getCurrentLegalMoves();
    //     for(Piece piece: moves.keySet()){
    //         HashSet<Move> pieceMoves = moves.get(piece);
    //         System.out.println(piece.name);
    //         for(Move move: pieceMoves){
    //             System.out.println("\t" + move);
    //         }
    //         System.out.println("=====================================");
    //     }
    // }

    /**
     * Checks whether this piece can move to this position. This function is called by the visuals to see
     * if a user-selected piece can move to a user-selected position
     * @param piece
     * @param position
     * @return 
     */
    public Move userValidMove(Piece piece, Square position){

        Move potentialMove = null;

        HashSet<Move> legalMovesForPiece = getCurrentLegalMoves().get(piece);
        
        for(Move move: legalMovesForPiece){
            if(position.equals(move.targetPosition)){
                potentialMove = new Move(move.piece, move.capturedPiece, move.previousPosition, move.targetPosition, move.moveType);
                if (areEnemies(piece, move.capturedPiece)) potentialMove.moveType = MoveType.CAPTURE;

                return potentialMove;
            }
        }

        return null;

    }

    /**
     * Applies this move to the board. Assumes that this move is already legal
     * @param piece
     * @param move
     * @param simulation
     * @return Returns this move with the updated castling, enpassant or promotion
     */
    public Move movePiece(Move move){
        
        Move result = null;

        Piece piece = move.piece;
        Piece capturedPiece = move.capturedPiece;
        Square previousPosition = move.previousPosition;
        Square targetPosition = move.targetPosition;
        MoveType moveType = move.moveType;
        Boolean enPassantSave = piece.enPassant;


        if ((moveType == MoveType.DEFAULT) || ( moveType == MoveType.CASTLE_KING_SIDE) || (moveType == MoveType.CASTLE_QUEEN_SIDE)){
            
            //if(targetPosition.col == -1 || targetPosition.row ==-1) System.out.println(move.piece.name + " , " + move.previousPosition + " " + move.targetPosition);
            setPiece(piece,targetPosition);
            setPiece(null,previousPosition);

            piece.enPassant = (EnumSet.of(Name.PAWNW,Name.PAWNB).contains(piece.name) && piece.firstMove && Math.abs(targetPosition.row-previousPosition.row)==2);
            result = new Move(piece,capturedPiece, previousPosition, targetPosition, moveType);

        }

        if ((moveType == MoveType.CASTLE_KING_SIDE) || (moveType == MoveType.CASTLE_QUEEN_SIDE)){
            //System.out.println(move);
            

            Piece rook = move.rookToCastle;
            previousPosition = move.rookPreviousPosition;
            Square targetPositionRook = move.rookTargetPosition;
            capturedPiece = getPiece(targetPositionRook);
            setPiece(rook,targetPositionRook);
            setPiece(null,previousPosition);

            rook.firstMove = false;
            result = move;
            
        }

        if (moveType == MoveType.PROMOTION){
            setPiece(piece, targetPosition);
            setPiece(null, previousPosition);

            piece.name = move.promotionName; //LET USER PICK

            piece.canSlide = true;

            result = new Move(piece, capturedPiece, previousPosition, targetPosition, moveType);
        }

        if (moveType == MoveType.ENPASSANT){
            Square enPassantSquare = new Square(targetPosition.col,previousPosition.row);
            capturedPiece = getPiece(enPassantSquare);
            setPiece(piece, targetPosition);
            setPiece(null, previousPosition);
            setPiece(null, enPassantSquare);

            result = new Move(piece, capturedPiece, previousPosition, targetPosition, moveType);
            result.enPassantPosition = enPassantSquare;
        }

        //Information to save
        result.enPassant = enPassantSave;
        result.firstMove = piece.firstMove;
        result.whiteTurn = this.WHITE_TURN;

        //Information to update
        this.WHITE_TURN = !this.WHITE_TURN;
        piece.firstMove = false;

        // this.updateCurrentLegalMoves();
        // this.updateGameStatus();
        //this.MOVE_HISTORY.put(this.MOVE_NUMBER, result);
        //this.MOVE_NUMBER += 1;

        // if((move.moveType == MoveType.CASTLE_KING_SIDE) || (move.moveType == MoveType.CASTLE_QUEEN_SIDE)){  
        //     printBoard(null);
        // }

        return result;
    }

    public void undoMove(Move move){
        Piece piece = move.piece;
        Piece capturedPiece = move.capturedPiece;
        Square previousPosition = move.previousPosition;
        Square targetPosition = move.targetPosition;
        MoveType moveType = move.moveType;
        this.WHITE_TURN = move.whiteTurn;

        if(piece.name != Name.PAWNW && piece.name != Name.PAWNB){
            piece.firstMove = move.firstMove;
        }

        

        piece.enPassant = move.enPassant;

        if(moveType == MoveType.DEFAULT || moveType == MoveType.CASTLE_KING_SIDE || moveType == MoveType.CASTLE_QUEEN_SIDE){
            setPiece(piece, previousPosition);
            setPiece(capturedPiece, targetPosition);

            piece.col = previousPosition.col;
            piece.row = previousPosition.row;

            if(piece.name == Name.PAWNW){
                piece.firstMove = previousPosition.row == 6;
            }

            if(piece.name == Name.PAWNB){
                piece.firstMove = previousPosition.row == 1;
            }

            if(moveType == MoveType.CASTLE_KING_SIDE || moveType == MoveType.CASTLE_QUEEN_SIDE){
                //System.out.println("UNDOING KING TO " + previousPosition + " UNDOING ROOK TO " + move.rookPreviousPosition);
                
                Piece rook = move.rookToCastle;
                previousPosition = move.rookPreviousPosition;
                targetPosition = move.rookTargetPosition;

                piece.firstMove = true;
                rook.firstMove = true;

                setPiece(rook,previousPosition);
                setPiece(capturedPiece,targetPosition);

                rook.col = previousPosition.col;
                rook.row = previousPosition.row;
            }
        }

        if(moveType == MoveType.ENPASSANT){
            Square enPassantPosition = move.enPassantPosition;
            setPiece(piece,previousPosition);
            setPiece(null,targetPosition);
            setPiece(capturedPiece,enPassantPosition);

            piece.col = previousPosition.col;
            piece.row = previousPosition.row;
        }

        if(moveType == MoveType.PROMOTION){
            setPiece(capturedPiece,targetPosition);
            setPiece(piece,previousPosition);

            piece.col = previousPosition.col;
            piece.row = previousPosition.row;

            piece.name = (targetPosition.row == 0) ? Name.PAWNW : Name.PAWNB;
            piece.canSlide = false;
        }

        // if(moveType == MoveType.CASTLE_KING_SIDE || moveType == MoveType.CASTLE_QUEEN_SIDE){  
        //     System.out.println("=============UNDOING KING=================");
        //     printBoard(null);
        // }
    }

    public GameState updateGameStatus(){



        return GameState.ACTIVE;

    }

    public HashSet<Move> legalMoves(Piece piece, Boolean checkKingSafety){

        HashMap<Piece, HashSet<Square>> pinnedPieces = updatePinnedPieces(piece.white);
        
        HashSet<Move> legalMoves = new HashSet<>();

        // Pawns and Kings are calculated separately
        if(piece.name == Name.PAWNW || piece.name == Name.PAWNB) return legalMovesPawn(piece, checkKingSafety, pinnedPieces);
        if (piece.name == Name.KING) return legalMovesKing(piece, checkKingSafety, pinnedPieces);
        
        HashSet<Square> displacements = this.PSEUDO_LEGAL_MOVEMENT.get(piece.name);

        for(Square displacement: displacements){

            //if(pinnedPieces.keySet().contains(piece) && !pinnedPieces.get(piece).contains(displacement)) continue;

            Square previousPosition = piece.getPosition();
            Square targetPosition = piece.getPosition();
            targetPosition = targetPosition.displace(displacement);

            while(validPosition(targetPosition)){

                Piece targetPiece = getPiece(targetPosition);

                Move potentialMove = new Move(piece,targetPiece,previousPosition,targetPosition,MoveType.DEFAULT);

                // Empty spaces or enemies are added as long as the king is safe after this move
                if((targetPiece == null || areEnemies(targetPiece, piece)) && kingSafeAfterMove(potentialMove, checkKingSafety)){
                    legalMoves.add(potentialMove);
                }

                // If a piece is in the way, or this piece cannot slide, break and try new direction
                if(targetPiece != null || !piece.canSlide) break;
                
                targetPosition = targetPosition.displace(displacement);
            }
        }


        return legalMoves;
    }

    public HashSet<Move> legalMovesKing(Piece king, Boolean checkKingSafety, HashMap<Piece, HashSet<Square>> pinnedPieces){

        HashSet<Move> kingMoves = new HashSet<>();
        HashSet<Square> displacements = this.PSEUDO_LEGAL_MOVEMENT.get(king.name);
        for(Square displacement: displacements){
            if(pinnedPieces.keySet().contains(king) && !pinnedPieces.get(king).contains(displacement)) continue;
            
            int dx = displacement.col;
            int dy = displacement.row;

            Square kingSquare = king.getPosition();
            Square targetSquare = kingSquare.displace(displacement);

            if(validPosition(targetSquare)){

                Piece targetPiece = getPiece(targetSquare);

                Move potentialMove = new Move(king,targetPiece,kingSquare,targetSquare,MoveType.DEFAULT);

                if((targetPiece==null || areEnemies(king,targetPiece)) && kingSafeAfterMove(potentialMove, checkKingSafety)){
                    kingMoves.add(potentialMove);

                    targetSquare = targetSquare.displace(new Square(dx,0));

                    if(king.firstMove && validPosition(targetSquare) && getPiece(targetSquare) == null && canRookCastle(king,dx)!=null){
                        MoveType moveType = (dx==1) ? MoveType.CASTLE_KING_SIDE:MoveType.CASTLE_QUEEN_SIDE;
                        potentialMove = new Move(king,null,kingSquare,targetSquare, moveType);
                        Square[] castleInfo = canRookCastle(king,dx);
                        potentialMove.rookToCastle = getPiece(castleInfo[0]);
                        potentialMove.rookPreviousPosition = castleInfo[0];
                        potentialMove.rookTargetPosition = castleInfo[1];

                        if((dx==-1 || dx == 1) && dy == 0  && kingSafeAfterMove(potentialMove, checkKingSafety)){
                            
                            kingMoves.add(potentialMove);
                        }

                    }
                        
                }   
            }
        }
        
        return kingMoves;

    }

    public HashSet<Move> legalMovesPawn(Piece pawn, Boolean checkKingSafety, HashMap<Piece, HashSet<Square>> pinnedPieces){

        HashSet<Move> pawnMoves = new HashSet<>();

        HashSet<Square> displacements = this.PSEUDO_LEGAL_MOVEMENT.get(pawn.name);

        for(Square displacement: displacements){
            
            if(pinnedPieces.get(pawn) != null && !pinnedPieces.get(pawn).contains(displacement)) continue; //If this piece is pinned, it can only move in pinned displacements

            int dx = displacement.col;
            int dy = displacement.row;

            Square prevPos = pawn.getPosition();
            int currCol = prevPos.col + dx;
            int currRow = prevPos.row + dy;

            if(!validPosition(new Square(currCol,currRow))) continue; //Move must be on the board

            MoveType moveType = ((pawn.white && currRow ==0) || (!pawn.white && currRow == 7)) ? MoveType.PROMOTION : MoveType.DEFAULT;
        
            Piece targetPiece = getPiece(new Square(currCol,currRow));

            Square targetPosition = new Square(currCol, currRow);

            Piece enPassantPiece = getPiece(new Square(currCol,prevPos.row));

            Move potentialMove = new Move(pawn, targetPiece, prevPos,targetPosition,moveType);

            if(dx==0 && targetPiece == null){
                if(kingSafeAfterMove(potentialMove, checkKingSafety)){
                    if(moveType == MoveType.PROMOTION){
                        Move promotionQueen = new Move(pawn, targetPiece, prevPos,targetPosition,moveType,Name.QUEEN);
                        Move promotionRook = new Move(pawn, targetPiece, prevPos,targetPosition,moveType,Name.ROOK);
                        Move promotionBishop = new Move(pawn, targetPiece, prevPos,targetPosition,moveType,Name.BISHOP);
                        Move promotionKnight = new Move(pawn, targetPiece, prevPos,targetPosition,moveType,Name.KNIGHT);
                        pawnMoves.add(promotionQueen);
                        pawnMoves.add(promotionRook);
                        pawnMoves.add(promotionBishop);
                        pawnMoves.add(promotionKnight);
                    }else{
                        pawnMoves.add(potentialMove);
                    }
                } 

                Square newTargetPosition = new  Square(currCol,currRow+dy);

                if(pawn.firstMove && validPosition(newTargetPosition) && getPiece(newTargetPosition) == null){
                    targetPiece = null;

                    Move doubleMove = new Move(pawn,targetPiece,prevPos,newTargetPosition,moveType);

                    if(kingSafeAfterMove(doubleMove, checkKingSafety)){
                        pawnMoves.add(doubleMove);
                    }
                }
            }


            if(dx!=0){

                if(areEnemies(pawn,targetPiece) && kingSafeAfterMove(potentialMove, checkKingSafety)){
                    if(moveType == MoveType.PROMOTION){
                        Move promotionQueen = new Move(pawn, targetPiece, prevPos,targetPosition,moveType,Name.QUEEN);
                        Move promotionRook = new Move(pawn, targetPiece, prevPos,targetPosition,moveType,Name.ROOK);
                        Move promotionBishop = new Move(pawn, targetPiece, prevPos,targetPosition,moveType,Name.BISHOP);
                        Move promotionKnight = new Move(pawn, targetPiece, prevPos,targetPosition,moveType,Name.KNIGHT);
                        pawnMoves.add(promotionQueen);
                        pawnMoves.add(promotionRook);
                        pawnMoves.add(promotionBishop);
                        pawnMoves.add(promotionKnight);
                    }else{
                        pawnMoves.add(potentialMove);
                    }
                }
                
                Move enPassantMove = new Move(pawn,enPassantPiece,prevPos,targetPosition,MoveType.ENPASSANT);

                if(targetPiece==null && areEnemies(enPassantPiece,pawn) && enPassantPiece != null && enPassantPiece.enPassant && kingSafeAfterMove(enPassantMove, checkKingSafety)){
                    pawnMoves.add(enPassantMove);
                }
            }
        
        }

        return pawnMoves;

    }

    public Boolean kingSafeAfterMove(Move move, Boolean checkKingSafety){

        if(!checkKingSafety) return true;
        
        Piece piece = move.piece;
        boolean kingSafeAfterMove = true;

        Move simulatedMove = movePiece(move);

        // Check if king is in the attacking squares of enemy pieces
        Square king = getKing(piece.white).getPosition();

        // Find all squares that enemies are attacking, no need to check for their king safety
        for(Piece enemy: getPieces(!piece.white)){
            HashSet<Move> enemyLegalMoves = legalMoves(enemy,false);

            for(Move enemyMove:enemyLegalMoves){
                if(enemyMove.targetPosition.equals(king)){
                    kingSafeAfterMove = false;
                    break;
                }
                if(!kingSafeAfterMove) break;
                
            }
            if(!kingSafeAfterMove) break;

        }

        

        undoMove(simulatedMove);

        return kingSafeAfterMove;

    }

    public HashMap<Piece, HashSet<Square>> updatePinnedPieces(boolean white){

        HashMap<Piece, HashSet<Square>> pinnedPieces = new HashMap<>();
        Piece king = getKing(white);
        HashSet<Square> displacements = this.PSEUDO_LEGAL_MOVEMENT.get(Name.QUEEN);

        Square target = king.getPosition();
        // For this king expand outwards in all 8 directions (queen movement)

        for(Square displacement: displacements){
            Piece potentialPin = null;
            target.displace(displacement);

            while(validPosition(target)){
                Piece targetPiece = getPiece(target);

                if(targetPiece == null){
                    target.displace(displacement);
                    continue;
                }

                if(areEnemies(targetPiece, king)){
                    if(potentialPin != null && targetPiece.canSlide && this.PSEUDO_LEGAL_MOVEMENT.get(targetPiece.name).contains(displacement)){
                        pinnedPieces.put(potentialPin,new HashSet<>(Arrays.asList(new Square(displacement.col,displacement.row), new Square(displacement.col * -1, displacement.row * -1))));
                    }
                    break;
                }else{
                    if(potentialPin != null) break;
                    else potentialPin = targetPiece;
                }

                target.displace(displacement);
            }

        }

        return pinnedPieces;
    }

    public Square[] canRookCastle(Piece king, int dx){
        Square[] result = null;
        Square whiteKingSideSquare = new Square(7,7);
        Square whiteKingSideTarget = new Square(5,7);

        Square whiteQueenSideSquare = new Square(0,7);
        Square whiteQueenSideTarget = new Square(3,7);

        Square blackKingSideSquare = new Square(7,0);
        Square blackKingSideTarget = new Square(5,0);

        Square blackQueenSideSquare = new Square(0,0);
        Square blackQueenSideTarget = new Square(3,0);

        Piece whiteKingSideRook = getPiece(whiteKingSideSquare);
        Piece whiteQueenSideRook = getPiece(whiteQueenSideSquare);

        Piece blackKingSideRook = getPiece(blackKingSideSquare);
        Piece blackQueenSideRook = getPiece(blackQueenSideSquare);

        if(king.white){
            if(dx==1 && whiteKingSideRook != null){
                result = new Square[]{whiteKingSideSquare,whiteKingSideTarget };
            }
            if(dx==-1 && whiteQueenSideRook != null){
                result = new Square[]{whiteQueenSideSquare, whiteQueenSideTarget };
            }
        }else{
            if(dx==1 && blackKingSideRook != null){
                result = new Square[]{blackKingSideSquare, blackKingSideTarget};
            }
            if(dx==-1 && blackQueenSideRook != null){
                result = new Square[]{blackQueenSideSquare, blackQueenSideTarget };
            }
        }

        return result;

    }

    public Piece getKing(Boolean white){

        for(Piece piece: getPieces(white)){
            if(piece.white == white && piece.name == Name.KING){
                return piece;
            }
        }
        
        throw new IllegalStateException("KING IS NOT FOUND");

    }

    public Boolean areEnemies(Piece a, Piece b){

        if (a==null || b==null) return false;
        return a.white != b.white;

    }

    public Piece getPiece(Square position){

        return this.BOARD[position.row][position.col];

    }

    /**
     * Sets this piece at this col and row on the board, overriding anything at this position
     * Updates this piece's col and row
     * @param newPiece
     * @param col
     * @param row
     */
    public void setPiece(Piece newPiece, Square position){

        this.BOARD[position.row][position.col] = newPiece;

        if(newPiece != null){
            newPiece.col = position.col;
            newPiece.row = position.row;
        }

    }

    public Boolean validPosition(Square position){

        return position.col <= 7 && position.col >= 0 && position.row <= 7 && position.row >=0;

    }

    public HashSet<Piece> getPieces(Boolean white){

        HashSet<Piece> pieces = new HashSet<>();

        for(int row=0;row<=7;row++){
            for(int col=0;col<=7;col++){
                Piece current = getPiece(new Square(col,row));
                if(current==null) continue;

                if(current.white == white){
                    pieces.add(current);
                }
            }
        }
        return pieces;

    }
}