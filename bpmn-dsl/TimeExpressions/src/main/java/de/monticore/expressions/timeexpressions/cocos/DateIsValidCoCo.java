package de.monticore.expressions.timeexpressions.cocos;

import com.google.common.base.Joiner;
import de.monticore.expressions.Messages;
import de.monticore.expressions.timeexpressions._ast.ASTDate;
import de.monticore.expressions.timeexpressions._cocos.TimeExpressionsASTDateCoCo;
import de.se_rwth.commons.logging.Log;

public class DateIsValidCoCo implements TimeExpressionsASTDateCoCo {

    @Override
    public void check(final ASTDate date) {
        try {
            date.getLocalDate();
        } catch (final IllegalArgumentException e) {
            Log.error(Messages.TEMP.err("0xTEMP04", Joiner.on(".")
                            .join(date.getYear().getSource(), date.getMonth().getSource(), date.getDay().getSource())),
                    date.get_SourcePositionStart()
            );
        }


    }

}
