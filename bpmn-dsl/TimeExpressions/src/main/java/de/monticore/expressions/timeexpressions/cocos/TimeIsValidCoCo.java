package de.monticore.expressions.timeexpressions.cocos;

import com.google.common.base.Joiner;
import de.monticore.expressions.Messages;
import de.monticore.expressions.timeexpressions._ast.ASTTime;
import de.monticore.expressions.timeexpressions._cocos.TimeExpressionsASTTimeCoCo;
import de.monticore.literals.literals._ast.ASTIntLiteral;
import de.se_rwth.commons.logging.Log;

public class TimeIsValidCoCo implements TimeExpressionsASTTimeCoCo {

    @Override
    public void check(final ASTTime time) {
        try {
            time.getLocalTime();
        } catch (final IllegalArgumentException e) {
            Log.error(Messages.TEMP.err("0xTEMP03", Joiner.on(":").skipNulls()
                            .join(time.getHours().getSource(), time.getMinutes().getSource(), time.getSecondsOpt().map(ASTIntLiteral::getSource).orElse(null))),
                    time.get_SourcePositionStart());
        }
    }

}
