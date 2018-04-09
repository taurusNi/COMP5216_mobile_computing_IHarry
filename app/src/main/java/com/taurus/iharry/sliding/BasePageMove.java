package com.taurus.iharry.sliding;

/**
 * Created by taurus on 16/9/9.
 */
public abstract class BasePageMove implements IPageMove{
        //the move direction of finger
        public static final int MOVE_TO_LEFT = 0;  // Move to next
        public static final int MOVE_TO_RIGHT = 1; // Move to previous
        public static final int MOVE_NO_RESULT = 4;


        static final int MODE_NONE = 0; //do not move
        static final int MODE_MOVE = 1; //move

}
