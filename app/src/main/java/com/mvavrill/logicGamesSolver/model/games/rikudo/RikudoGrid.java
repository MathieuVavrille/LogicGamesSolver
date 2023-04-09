package com.mvavrill.logicGamesSolver.model.games.rikudo;

import androidx.annotation.NonNull;

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

    public RikudoGrid<T> copy() {
        return new RikudoGrid<>(grid.stream().map(ArrayList::new).collect(Collectors.toList()), new ArrayList<>(fixedEdges));
    }

    public List<List<T>> getGrid() {
        return grid;
    }

    public List<Quartet<Integer, Integer, Integer, Integer>> getFixedEdges() {
        return fixedEdges;
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
        return new RikudoGrid<>(newGrid, fixedEdges.stream()
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
        List<DigitCell> addedLine = new ArrayList<>();
        boolean[] hints = new boolean[]{true};
        for (int i = 0; i < grid.get(0).size()+1; i++) {
            addedLine.add(new DigitCell(hints));
        }
        newGrid.add(addedLine);
        for (int i = 0; i < grid.size(); i++) {
            List<DigitCell> newLine = new ArrayList<>(grid.get(i));
            newLine.add(0, new DigitCell(hints)); // pop last element
            newLine.add(new DigitCell(hints)); // pop last element
            newGrid.add(newLine);
        }
        newGrid.add(addedLine);
        return new RikudoGrid<>(newGrid, smallGrid.getFixedEdges());
    }

    public static RikudoGrid<DigitCell> generateInitialGrid() {
        boolean[] hints = new boolean[]{true};
        return new RikudoGrid<>(List.of(
                List.of(new DigitCell(hints), new DigitCell(hints), new DigitCell(hints)),
                List.of(new DigitCell(hints), new DigitCell(hints), new DigitCell(hints), new DigitCell(hints)),
                List.of(new DigitCell(hints), new DigitCell(hints), new DigitCell(hints), new DigitCell(hints), new DigitCell(hints)),
                List.of(new DigitCell(hints), new DigitCell(hints), new DigitCell(hints), new DigitCell(hints)),
                List.of(new DigitCell(hints), new DigitCell(hints), new DigitCell(hints))
        ), new ArrayList<>());
    }

    public static RikudoGrid<Integer> extractFixedCells(final RikudoGrid<DigitCell> cellGrid) {
        return new RikudoGrid<>(cellGrid.getGrid().stream()
                .map(line -> line.stream().map(cell -> cell.isFixed() ? cell.getValue() : 0).collect(Collectors.toList()))
                .collect(Collectors.toList()), new ArrayList<>(cellGrid.getFixedEdges()));
    }

    @NonNull
    @Override
    public String toString() {
        return grid + " " + fixedEdges;
    }
}
