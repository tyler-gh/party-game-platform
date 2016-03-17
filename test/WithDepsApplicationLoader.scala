/**
  * Created by Luke on 3/10/2016.
  */
import play.api.test.WithApplicationLoader
class WithDepsApplicationLoader extends WithApplicationLoader(new PGPApplicationLoader) {

}
