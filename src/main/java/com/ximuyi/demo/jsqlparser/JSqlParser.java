package com.ximuyi.demo.jsqlparser;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.*;
import net.sf.jsqlparser.expression.operators.arithmetic.*;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.conditional.OrExpression;
import net.sf.jsqlparser.expression.operators.relational.*;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SubSelect;

/**
 * Created by chenjingjun on 2018-03-07.
 */
public class JSqlParser {

    /**
     *
     * @param args
     */
    public static void main(String[] args) throws JSQLParserException {
        Statement stat = CCJSqlParserUtil.parse(String.format("select * from MonsterRun where %s", "(Lv>10 and Lv<20) or Color='RED'"));
        Select select = (Select) stat;
        Expression expression = ((PlainSelect) select.getSelectBody()).getWhere();
        WhereExpressionVisitor visitor = new WhereExpressionVisitor();
        expression.accept(visitor);
    }

    private static class WhereExpressionVisitor implements ExpressionVisitor{

        @Override
        public void visit(NullValue nullValue) {

        }

        @Override
        public void visit(Function function) {

        }

        @Override
        public void visit(SignedExpression signedExpression) {

        }

        @Override
        public void visit(JdbcParameter jdbcParameter) {

        }

        @Override
        public void visit(JdbcNamedParameter jdbcNamedParameter) {

        }

        @Override
        public void visit(DoubleValue doubleValue) {

        }

        @Override
        public void visit(LongValue longValue) {

        }

        @Override
        public void visit(HexValue hexValue) {

        }

        @Override
        public void visit(DateValue dateValue) {

        }

        @Override
        public void visit(TimeValue timeValue) {

        }

        @Override
        public void visit(TimestampValue timestampValue) {

        }

        @Override
        public void visit(Parenthesis parenthesis) {
            parenthesis.getExpression().accept(this);
        }

        @Override
        public void visit(StringValue stringValue) {

        }

        @Override
        public void visit(Addition addition) {

        }

        @Override
        public void visit(Division division) {

        }

        @Override
        public void visit(Multiplication multiplication) {

        }

        @Override
        public void visit(Subtraction subtraction) {

        }

        @Override
        public void visit(AndExpression andExpression) {
            Expression leftExpression = andExpression.getLeftExpression();
            leftExpression.accept(this);
            Expression rightExpression = andExpression.getRightExpression();
            rightExpression.accept(this);
        }

        @Override
        public void visit(OrExpression orExpression) {
            Expression leftExpression = orExpression.getLeftExpression();
            leftExpression.accept(this);
            Expression rightExpression = orExpression.getRightExpression();
            rightExpression.accept(this);
        }

        @Override
        public void visit(Between between) {

        }

        @Override
        public void visit(EqualsTo equalsTo) {

        }

        @Override
        public void visit(GreaterThan greaterThan) {

        }

        @Override
        public void visit(GreaterThanEquals greaterThanEquals) {

        }

        @Override
        public void visit(InExpression inExpression) {

        }

        @Override
        public void visit(IsNullExpression isNullExpression) {

        }

        @Override
        public void visit(LikeExpression likeExpression) {

        }

        @Override
        public void visit(MinorThan minorThan) {

        }

        @Override
        public void visit(MinorThanEquals minorThanEquals) {

        }

        @Override
        public void visit(NotEqualsTo notEqualsTo) {

        }

        @Override
        public void visit(Column column) {

        }

        @Override
        public void visit(SubSelect subSelect) {

        }

        @Override
        public void visit(CaseExpression caseExpression) {

        }

        @Override
        public void visit(WhenClause whenClause) {

        }

        @Override
        public void visit(ExistsExpression existsExpression) {

        }

        @Override
        public void visit(AllComparisonExpression allComparisonExpression) {

        }

        @Override
        public void visit(AnyComparisonExpression anyComparisonExpression) {

        }

        @Override
        public void visit(Concat concat) {

        }

        @Override
        public void visit(Matches matches) {

        }

        @Override
        public void visit(BitwiseAnd bitwiseAnd) {

        }

        @Override
        public void visit(BitwiseOr bitwiseOr) {

        }

        @Override
        public void visit(BitwiseXor bitwiseXor) {

        }

        @Override
        public void visit(CastExpression castExpression) {

        }

        @Override
        public void visit(Modulo modulo) {

        }

        @Override
        public void visit(AnalyticExpression analyticExpression) {

        }

        @Override
        public void visit(WithinGroupExpression withinGroupExpression) {

        }

        @Override
        public void visit(ExtractExpression extractExpression) {

        }

        @Override
        public void visit(IntervalExpression intervalExpression) {

        }

        @Override
        public void visit(OracleHierarchicalExpression oracleHierarchicalExpression) {

        }

        @Override
        public void visit(RegExpMatchOperator regExpMatchOperator) {

        }

        @Override
        public void visit(JsonExpression jsonExpression) {

        }

        @Override
        public void visit(JsonOperator jsonOperator) {

        }

        @Override
        public void visit(RegExpMySQLOperator regExpMySQLOperator) {

        }

        @Override
        public void visit(UserVariable userVariable) {

        }

        @Override
        public void visit(NumericBind numericBind) {

        }

        @Override
        public void visit(KeepExpression keepExpression) {

        }

        @Override
        public void visit(MySQLGroupConcat mySQLGroupConcat) {

        }

        @Override
        public void visit(RowConstructor rowConstructor) {

        }

        @Override
        public void visit(OracleHint oracleHint) {

        }

        @Override
        public void visit(TimeKeyExpression timeKeyExpression) {

        }

        @Override
        public void visit(DateTimeLiteralExpression dateTimeLiteralExpression) {

        }

        @Override
        public void visit(NotExpression notExpression) {

        }
    }
}
