package com.opedea.datasource;

import java.util.ArrayList;

public class Where {
    private ArrayList<ArrayList<Clause>> whereClauses= new ArrayList<>();
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

        public String getLeft() {
            return left;
        }

        public String getRight() {
            return right;
        }

        public Type getType() {
            return type;
        }

        public Clause and(Clause next){
            next.prev=this;
            return next;
        }
        public static Clause lt(String left, String right){
            return new Clause(left,right,Type.LESS_THAN);
        }
        public static Clause gt(String left,String right){
            return new Clause(left,right,Type.GREATER_THAN);
        }
        public static Clause gte(String left,String right){
            return new Clause(left,right,Type.GREATER_THAN_OR_EQUAL_TO);
        }
        public static Clause lte(String left,String right){
            return new Clause(left,right,Type.LESS_THAN_OR_EQUAL_TO);
        }
        public static Clause eq(String left,String right){
            return new Clause(left,right,Type.EQUAL_TO);
        }

        public static Clause neq(String left,String right){
            return new Clause(left,right,Type.NOT_EQUAL_TO);
        }

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
