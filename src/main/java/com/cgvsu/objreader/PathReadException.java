package com.cgvsu.objreader;

/** @author <a href="https://github.com/arda-sketch/CG3.ObjReader">Артамонов Артём Артурович</a> */

public class PathReadException extends Exception {
    public PathReadException() {
        super("Error in file path, pls check it");
    }
}