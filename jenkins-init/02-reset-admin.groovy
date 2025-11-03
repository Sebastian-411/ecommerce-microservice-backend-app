import jenkins.model.*
import hudson.security.*

def j = Jenkins.getInstance()
def realm = j.getSecurityRealm()
if (!(realm instanceof HudsonPrivateSecurityRealm)) {
  realm = new HudsonPrivateSecurityRealm(false)
  j.setSecurityRealm(realm)
}

def u = realm.getUser("admin")
if (u == null) {
  realm.createAccount("admin","admin123")
  println("Usuario admin creado")
} else {
  u.changePassword("admin123")
  println("Password de admin reseteado")
}

def strategy = new FullControlOnceLoggedInAuthorizationStrategy()
strategy.setAllowAnonymousRead(false)
j.setAuthorizationStrategy(strategy)
j.save()
