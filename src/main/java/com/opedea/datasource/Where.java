package com.opedea.datasource;

import java.util.ArrayList;

/**
 * Class that handles all the where clauses in a simple manner for both sides
 */
public class Where {
    private ArrayList<ArrayList<Clause>> whereClauses= new ArrayList<>();

    /**
     * adds one or more clauses to the where
     * @param orClauses a list of clauses seperated by OR
     * @see Clause
     * @return the object, for chaining purposes.
     */
    public Where or(Clause... orClauses){
        for (Clause clause : orClauses) {
            ArrayList<Clause> andClause = new ArrayList<>();
            Clause c = clause;
            andClause.add(c);//Adding here in case there is no next

            while(c.hasNext()){
                c= c.prev;
                andClause.add(c);
            }
            whereClauses.add(andClause);
        }
        return this;
    }

    /**
     * returns the where clauses
     * @return a list (OR between items) of lists (AND between items) of clauses
     */
    public ArrayList<ArrayList<Clause>> getWhereClauses() {
        return whereClauses;
    }

    /**
     * returns a standart sql where statement
     * @return sql statemebt Ex.(WHERE a)
     */
    public String generateSQLQuery(){
        StringBuilder sb = new StringBuilder();
        sb.append("WHERE ");
        for (int i = 0; i < whereClauses.size(); i++) {
            ArrayList<Clause> clauses = whereClauses.get(i);
            if(i>0) sb.append("(");
            for (int j = 0; j < clauses.size(); j++) {
                Clause clause = clauses.get(j);
                sb.append(clause.left);
                sb.append(" ");
                sb.append(clause.type.toSymbolString());
                sb.append(" ");
                sb.append(clause.right);
                sb.append(" ");
                if((j+1)<clauses.size()) sb.append("AND ");
            }
            if(i>0) sb.append(")");
            sb.append(" ");
            if((i+1)<whereClauses.size()) sb.append("OR ");
        }

        return sb.toString();
    }

    /**
     * Helper class to explain a single boolean expression
     */
    public static class Clause{
        private final String left;
        private final String right;
        private final Type type;
        private Clause prev;

        private Clause(String left, String right, Type type){

            this.left = left;
            this.right = right;
            this.type = type;
        }
        boolean hasNext(){
            return prev!=null;
        }

        /**
         *
         * @return the left part of a boolean expression (left == right)
         */
        public String getLeft() {
            return left;
        }

        /**
         *
         * @return the right part of a boolean expression (left == right)
         */
        public String getRight() {
            return right;
        }

        /**
         *
         * @return the type of the boolean expression (the 'middle' part, left == right)
         */
        public Type getType() {
            return type;
        }

        /**
         * chain another clause in an AND relationship
         * @param next the clause to chain
         * @return the clause chained
         */
        public Clause and(Clause next){
            next.prev=this;
            return next;
        }

        /**
         * Creates a clause of less than type boolean expression
         * @param left left part of boolean expression, a field of a model
         * @param right right part of a boolean expression,
         * @return Clause representing this boolean expression
         */
        public static Clause lt(String left, String right){
            return new Clause(left,right,Type.LESS_THAN);
        }

        /**
         * Creates a clause of greater than type boolean expression
         * @param left left part of boolean expression, a field of a model
         * @param right right part of a boolean expression,
         * @return Clause representing this boolean expression
         */
        public static Clause gt(String left,String right){
            return new Clause(left,right,Type.GREATER_THAN);
        }

        /**
         * Creates a clause of greater than or equal to type boolean expression
         * @param left left part of boolean expression, a field of a model
         * @param right right part of a boolean expression,
         * @return Clause representing this boolean expression
         */
        public static Clause gte(String left,String right){
            return new Clause(left,right,Type.GREATER_THAN_OR_EQUAL_TO);
        }

        /**
         * Creates a clause of less than or equal to type boolean expression
         * @param left left part of boolean expression, a field of a model
         * @param right right part of a boolean expression,
         * @return Clause representing this boolean expression
         */
        public static Clause lte(String left,String right){
            return new Clause(left,right,Type.LESS_THAN_OR_EQUAL_TO);
        }

        /**
         * Creates a clause of equal type boolean expression
         * @param left left part of boolean expression, a field of a model
         * @param right right part of a boolean expression,
         * @return Clause representing this boolean expression
         */
        public static Clause eq(String left,String right){
            return new Clause(left,right,Type.EQUAL_TO);
        }

        /**
         * Creates a clause of not equal type boolean expression
         * @param left left part of boolean expression, a field of a model
         * @param right right part of a boolean expression,
         * @return Clause representing this boolean expression
         */
        public static Clause neq(String left,String right){
            return new Clause(left,right,Type.NOT_EQUAL_TO);
        }

        /**
         * All the supported comparisons
         */
        enum Type{
            LESS_THAN,GREATER_THAN,EQUAL_TO,LESS_THAN_OR_EQUAL_TO, NOT_EQUAL_TO, GREATER_THAN_OR_EQUAL_TO;


            public String toSymbolString() {
                switch (this){
                    case EQUAL_TO:return "="; 
                    case LESS_THAN:return "<"; 
                    case GREATER_THAN:return ">"; 
                    case NOT_EQUAL_TO:return "<>"; 
                    case LESS_THAN_OR_EQUAL_TO:return "<=";
                    case GREATER_THAN_OR_EQUAL_TO:return ">="; 
                    default: return null;
                }
            }
        }
    }

}
