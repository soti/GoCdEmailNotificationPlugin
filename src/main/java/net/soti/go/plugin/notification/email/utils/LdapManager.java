package net.soti.go.plugin.notification.email.utils;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;
import java.util.Hashtable;

import net.soti.go.plugin.notification.email.EmailNotificationPlugin;

import com.thoughtworks.go.plugin.api.logging.Logger;

/**
 * User: wsim
 * Date: 2018-04-04
 */
public class LdapManager {
    private static final Logger LOG = Logger.getLoggerFor(LdapManager.class);
    private final Hashtable<String, Object> env = new Hashtable<>();

    public LdapManager(String ldapAdServer, String ldapUser, String ldapPassword) {
        env.put(Context.SECURITY_AUTHENTICATION, "simple");
        if (ldapUser != null) {
            env.put(Context.SECURITY_PRINCIPAL, ldapUser);
        }
        if (ldapPassword != null) {
            env.put(Context.SECURITY_CREDENTIALS, ldapPassword);
        }
        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.PROVIDER_URL, ldapAdServer);
    }

    public synchronized String findEmailByAccountName(String accountName) {
        if (!EmailNotificationPlugin.EMAIL_CACHE.containsKey(accountName)) {
            String email = findEmailInternal(accountName);
            if (email != null && email.length() > 0) {
                EmailNotificationPlugin.EMAIL_CACHE.put(accountName, email);
            } else {
                return "";
            }
        }

        return EmailNotificationPlugin.EMAIL_CACHE.get(accountName);
    }

    private String findEmailInternal(String accountName) {
        try {
            LdapContext ctx = new InitialLdapContext(env, null);
            String searchFilter = "(&(objectClass=user)(sAMAccountName=" + accountName + "))";
            SearchControls searchControls = new SearchControls();
            searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
            NamingEnumeration<SearchResult> results = ctx.search("DC=corp,DC=soti,DC=net", searchFilter, searchControls);

            SearchResult searchResult;
            if (results.hasMoreElements()) {
                searchResult = results.nextElement();
            } else {
                return null;
            }

            Attributes attributes = searchResult.getAttributes();
            Attribute emailAttribute = attributes.get("mail");
            if (emailAttribute == null) {
                return null;
            }

            if (emailAttribute.get() == null) {
                return null;
            }

            return emailAttribute.get().toString();
        } catch (Exception e) {
            LOG.error(String.format("Failed to read email of user ", accountName), e);
            return "";
        }
    }
}
