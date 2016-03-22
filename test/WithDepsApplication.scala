import java.io.File

import play.api.test.WithApplicationLoader

class WithDepsApplication(loader:PGPApplicationLoader = new PGPApplicationLoader(new File(".").getAbsoluteFile.getParentFile.getParentFile.getParentFile)) extends WithApplicationLoader(loader)