package is.stma.judgebean.rest;

import javax.inject.Inject;
import javax.validation.Validator;
import java.util.logging.Logger;

abstract class AREST {

    @Inject
    Logger log;

    @Inject
    Validator validator;
}
