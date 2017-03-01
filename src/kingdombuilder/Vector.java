package kingdombuilder;

public class Vector implements Comparable {

    private int _x, _y;

    public Vector(int x, int y) {
        _x = x;
        _y = y;
    }
    
    public Vector(int x) {
    	_x = x;
    	_y = x;
    }

    public int getX() {
        return _x;
    }

    public int getY() {
        return _y;
    }

    public void setX(int x) {
        _x = x;
    }

    public void setY(int y) {
        _y = y;
    }

    public void set(int x, int y) {
        _x = x;
        _y = y;
    }

    public float distance(Vector other) {
        return (float) Math.sqrt(Math.pow(_x - other.getX(), 2) + Math.pow(_y - other.getY(), 2));
    }

    @Override
    public Vector clone() {
        return new Vector(_x, _y);
    }

    @Override
    public int compareTo(Object other) {
        if (other instanceof Vector) {
            return new Integer(_x * _y).compareTo(new Integer(((Vector) other).getX() * ((Vector) other).getY()));
        }
        return 0;
    }

    @Override
    public String toString() {
        return "(" + _x + ", " + _y + ")";
    }

    public Vector add(Vector other) {
        return new Vector(_x + other.getX(), _y + other.getY());
    }

    public Vector subtract(Vector other) {
        return new Vector(_x - other.getX(), _y - other.getY());
    }

    public Vector multiply(Vector other) {
        return new Vector(_x * other.getX(), _y * other.getY());
    }

    public Vector divide(Vector other) {
        return new Vector(_x / other.getX(), _y / other.getY());
    }
    
    public Vector divide(int num) {
        return new Vector(_x / num, _y / num);
    }
    
    public static Vector midpoint(Vector one, Vector two)
    {
    	return new Vector((one.getX() + two.getX()) / 2, (one.getY() + two.getY()) / 2); 
    }
}
