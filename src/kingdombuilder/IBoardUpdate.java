package kingdombuilder;

public interface IBoardUpdate {
	int check(Player player, Board board, Tile tile, int x, int y);
	void update(Game game, Board board, Player player, int[][] renderStates);
	String getTooltip();
	void activated(Game game, Board board);
}
