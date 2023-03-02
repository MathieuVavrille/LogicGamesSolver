package com.mvavrill.logicGamesSolver.model.games.rikudo;

import com.mvavrill.logicGamesSolver.model.cells.DigitCell;

import org.javatuples.Quartet;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class RikudoGrid<T> {
    private final List<List<T>> grid;
    private final List<Quartet<Integer,Integer,Integer,Integer>> fixedEdges;

    public RikudoGrid(final List<List<T>> grid, final List<Quartet<Integer,Integer,Integer,Integer>> fixedEdges) {
        this.grid = grid;
        this.fixedEdges = fixedEdges;
    }

    public RikudoGrid copy() {
        return new RikudoGrid(grid.stream().map(ArrayList::new).collect(Collectors.toList()), new ArrayList<>(fixedEdges));
    }

    public List<List<T>> getGrid() {
        return grid;
    }

    public List<Quartet<Integer, Integer, Integer, Integer>> getFixedEdges() {
        return fixedEdges;
    }

    public static RikudoGrid<Integer> extractFixed(final RikudoGrid<DigitCell> cellGrid) {
        return new RikudoGrid<Integer>(cellGrid.getGrid().stream()
                .map(line -> line.stream().map(cell -> cell.isFixed() ? cell.getValue() : 0).collect(Collectors.toList()))
                .collect(Collectors.toList()), cellGrid.getFixedEdges());
    }

    /** Removes one layer, on the right and the bottom */
    public RikudoGrid<T> decreaseSize() {
        List<List<T>> newGrid = new ArrayList<>();
        for (int i = 1; i < grid.size()-1; i++) { // We remove the extremities
            List<T> newLine = new ArrayList<>(grid.get(i));
            newLine.remove(0); // pop last element
            newLine.remove(newLine.size()-1); // pop last element
            newGrid.add(newLine);
        }
        return new RikudoGrid<T>(newGrid, fixedEdges.stream()
                .filter(q -> q.getValue0() < newGrid.size()
                        && q.getValue1() < newGrid.get(q.getValue1()).size()
                        && q.getValue2() < newGrid.size()
                        && q.getValue3() < newGrid.get(q.getValue2()).size())
                .collect(Collectors.toList()));
    }

    /** Adds one layer, on the right and the bottom
     * It is not generic because I need to create a new element*/
    public static RikudoGrid<DigitCell> increaseSize(final RikudoGrid<DigitCell> smallGrid) {
        List<List<DigitCell>> grid = smallGrid.getGrid();
        List<List<DigitCell>> newGrid = new ArrayList<>();
        List<DigitCell> addedLine = new ArrayList<DigitCell>();
        for (int i = 0; i < grid.get(0).size()+1; i++) {
            addedLine.add(new DigitCell(false, 0));
        }
        newGrid.add(addedLine);
        for (int i = 0; i < grid.size(); i++) {
            List<DigitCell> newLine = new ArrayList<>(grid.get(i));
            newLine.add(0, new DigitCell(false, 0)); // pop last element
            newLine.add(new DigitCell(false, 0)); // pop last element
            newGrid.add(newLine);
        }
        newGrid.add(addedLine);
        return new RikudoGrid<DigitCell>(newGrid, smallGrid.getFixedEdges());
    }

    public static RikudoGrid<DigitCell> generateInitialGrid() {
        return new RikudoGrid<DigitCell>(List.of(
                List.of(new DigitCell(false,0),new DigitCell(false,0),new DigitCell(false,0)),
                List.of(new DigitCell(false,0),new DigitCell(false,0),new DigitCell(false,0),new DigitCell(false,0)),
                List.of(new DigitCell(false,0),new DigitCell(false,0),new DigitCell(false,0),new DigitCell(false,0),new DigitCell(false,0)),
                List.of(new DigitCell(false,0),new DigitCell(false,0),new DigitCell(false,0),new DigitCell(false,0)),
                List.of(new DigitCell(false,0),new DigitCell(false,0),new DigitCell(false,0))
        ), new ArrayList<>());
    }

    public static RikudoGrid<Integer> extractCells(final RikudoGrid<DigitCell> cellGrid) {
        return new RikudoGrid<Integer>(cellGrid.getGrid().stream()
                .map(line -> line.stream().map(DigitCell::getValue).collect(Collectors.toList()))
                .collect(Collectors.toList()), new ArrayList<>(cellGrid.getFixedEdges()));
    }
}
