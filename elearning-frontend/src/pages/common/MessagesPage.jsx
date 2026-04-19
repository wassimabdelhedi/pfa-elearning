import { useState, useEffect, useRef } from 'react';
import { useAuth } from '../../context/AuthContext';
import { getConversations, getConversationHistory, sendMessage, searchContacts } from '../../api/messageApi';
import { FiSend, FiUser, FiSearch, FiMessageSquare } from 'react-icons/fi';

export default function MessagesPage() {
  const { user } = useAuth();
  const [conversations, setConversations] = useState([]);
  const [activeContact, setActiveContact] = useState(null);
  const [messages, setMessages] = useState([]);
  const [newMessage, setNewMessage] = useState('');
  const [searchQuery, setSearchQuery] = useState('');
  const [searchResults, setSearchResults] = useState([]);
  const [isSearching, setIsSearching] = useState(false);
  const messagesEndRef = useRef(null);

  useEffect(() => {
    loadConversations();
  }, []);

  useEffect(() => {
    if (activeContact) {
      loadHistory(activeContact.id || activeContact.contactId);
    }
  }, [activeContact]);

  useEffect(() => {
    scrollToBottom();
  }, [messages]);

  const loadConversations = async () => {
    try {
      const res = await getConversations();
      setConversations(res.data);
    } catch (err) {
      console.error('Failed to load conversations', err);
    }
  };

  const loadHistory = async (contactId) => {
    try {
      const res = await getConversationHistory(contactId);
      setMessages(res.data);
      // Also reload conversations to clear unread counts
      loadConversations();
    } catch (err) {
      console.error('Failed to load history', err);
    }
  };

  useEffect(() => {
    const delayDebounceFn = setTimeout(() => {
      if (searchQuery.trim()) {
        executeSearch(searchQuery);
      } else {
        setSearchResults([]);
        setIsSearching(false);
      }
    }, 300);

    return () => clearTimeout(delayDebounceFn);
  }, [searchQuery]);

  const executeSearch = async (query) => {
    setIsSearching(true);
    try {
      const res = await searchContacts(query);
      setSearchResults(res.data);
    } catch (err) {
      console.error('Search failed', err);
      setSearchResults([]);
    }
  };

  const handleSearch = (e) => {
    e.preventDefault();
    if (searchQuery.trim()) {
      executeSearch(searchQuery);
    }
  };

  const handleSend = async (e) => {
    e.preventDefault();
    if (!newMessage.trim() || !activeContact) return;

    const contactId = activeContact.id || activeContact.contactId;

    try {
      await sendMessage({
        receiverId: contactId,
        content: newMessage
      });
      setNewMessage('');
      loadHistory(contactId);
    } catch (err) {
      console.error('Failed to send message', err);
    }
  };

  const scrollToBottom = () => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  };

  const selectContact = (contact) => {
    setActiveContact(contact);
    setIsSearching(false);
    setSearchQuery('');
    setSearchResults([]);
  };

  return (
    <div className="page" style={{ height: 'calc(100vh - 64px)', padding: '24px', display: 'flex', flexDirection: 'column' }}>
      <div className="page-header" style={{ marginBottom: 16 }}>
        <h1 style={{ fontSize: '1.8rem' }}>✉️ Messagerie</h1>
      </div>

      <div className="card" style={{ flex: 1, padding: 0, display: 'flex', overflow: 'hidden', minHeight: 400 }}>
        
        {/* Left Sidebar: Conversations & Search */}
        <div style={{ width: 320, borderRight: '1px solid rgba(255,255,255,0.06)', display: 'flex', flexDirection: 'column', background: 'rgba(0,0,0,0.1)' }}>
          
          <div style={{ padding: 16, borderBottom: '1px solid rgba(255,255,255,0.06)' }}>
            <form onSubmit={handleSearch} style={{ position: 'relative' }}>
              <input
                type="text"
                placeholder={user?.role === 'STUDENT' ? "Chercher un enseignant..." : "Chercher un étudiant..."}
                value={searchQuery}
                onChange={(e) => setSearchQuery(e.target.value)}
                className="form-input"
                style={{ width: '100%', paddingLeft: 36, borderRadius: 100 }}
              />
              <button type="submit" style={{ position: 'absolute', left: 12, top: 12, background: 'none', color: 'var(--text-muted)' }}>
                <FiSearch size={16} />
              </button>
            </form>
          </div>

          <div style={{ flex: 1, overflowY: 'auto' }}>
            {isSearching ? (
              <div>
                <div style={{ padding: '8px 16px', fontSize: '0.8rem', color: 'var(--text-secondary)' }}>Résultats de recherche</div>
                {searchResults.length > 0 ? (
                  searchResults.map(result => (
                    <div 
                      key={result.id} 
                      onClick={() => selectContact(result)}
                      style={{ padding: '12px 16px', display: 'flex', alignItems: 'center', gap: 12, cursor: 'pointer', borderBottom: '1px solid rgba(255,255,255,0.02)' }}
                    >
                      <div style={{ width: 40, height: 40, borderRadius: '50%', background: 'var(--primary-500)', display: 'flex', alignItems: 'center', justifyContent: 'center', flexShrink: 0 }}>
                        <FiUser color="white" />
                      </div>
                      <div style={{ overflow: 'hidden' }}>
                        <div style={{ fontWeight: 600, whiteSpace: 'nowrap', textOverflow: 'ellipsis', overflow: 'hidden' }}>{result.name}</div>
                        <div style={{ fontSize: '0.8rem', color: 'var(--text-muted)' }}>{result.role === 'TEACHER' ? 'Enseignant' : 'Étudiant'}</div>
                      </div>
                    </div>
                  ))
                ) : (
                  <div style={{ padding: 20, textAlign: 'center', color: 'var(--text-muted)' }}>Aucun utilisateur trouvé.</div>
                )}
              </div>
            ) : (
              <div>
                {conversations.length > 0 ? (
                  conversations.map(conv => {
                    const isActive = activeContact && (activeContact.contactId === conv.contactId || activeContact.id === conv.contactId);
                    return (
                      <div 
                        key={conv.contactId}
                        onClick={() => selectContact(conv)}
                        style={{ 
                          padding: '16px', 
                          display: 'flex', 
                          gap: 12, 
                          cursor: 'pointer',
                          borderBottom: '1px solid rgba(255,255,255,0.02)',
                          background: isActive ? 'rgba(99, 102, 241, 0.1)' : 'transparent',
                          borderLeft: isActive ? '3px solid var(--primary-500)' : '3px solid transparent'
                        }}
                      >
                        <div style={{ width: 45, height: 45, borderRadius: '50%', background: 'rgba(255,255,255,0.1)', display: 'flex', alignItems: 'center', justifyContent: 'center', flexShrink: 0 }}>
                          <FiUser size={20} />
                        </div>
                        <div style={{ flex: 1, overflow: 'hidden' }}>
                          <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: 4 }}>
                            <div style={{ fontWeight: isActive ? 700 : 600, color: 'var(--text-primary)', whiteSpace: 'nowrap', textOverflow: 'ellipsis', overflow: 'hidden' }}>
                              {conv.contactName}
                            </div>
                            {conv.unreadCount > 0 && (
                              <span style={{ background: '#ef4444', color: '#fff', fontSize: '0.7rem', padding: '2px 6px', borderRadius: 10, fontWeight: 'bold' }}>
                                {conv.unreadCount}
                              </span>
                            )}
                          </div>
                          <div style={{ fontSize: '0.85rem', color: conv.unreadCount > 0 ? 'var(--text-primary)' : 'var(--text-muted)', whiteSpace: 'nowrap', textOverflow: 'ellipsis', overflow: 'hidden', fontWeight: conv.unreadCount > 0 ? 600 : 400 }}>
                            {conv.lastMessage}
                          </div>
                        </div>
                      </div>
                    );
                  })
                ) : (
                  <div style={{ padding: 40, textAlign: 'center', color: 'var(--text-muted)' }}>
                    <FiMessageSquare size={32} style={{ marginBottom: 12, opacity: 0.5 }} />
                    <p>Aucune conversation.</p>
                    <p style={{ fontSize: '0.85rem' }}>Utilisez la recherche ci-dessus pour envoyer un message.</p>
                  </div>
                )}
              </div>
            )}
          </div>
        </div>

        {/* Right Area: Active Chat */}
        <div style={{ flex: 1, display: 'flex', flexDirection: 'column', background: 'rgba(0,0,0,0.2)' }}>
          {activeContact ? (
            <>
              {/* Chat Header */}
              <div style={{ padding: '16px 24px', borderBottom: '1px solid rgba(255,255,255,0.06)', display: 'flex', alignItems: 'center', gap: 12, background: 'rgba(255,255,255,0.02)' }}>
                <div style={{ width: 40, height: 40, borderRadius: '50%', background: 'var(--primary-500)', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
                  <FiUser color="white" />
                </div>
                <div>
                  <h3 style={{ fontSize: '1.1rem', margin: 0 }}>{activeContact.contactName || activeContact.name}</h3>
                  <div style={{ fontSize: '0.8rem', color: 'var(--text-muted)' }}>{activeContact.contactRole || activeContact.role}</div>
                </div>
              </div>

              {/* Messages Area */}
              <div style={{ flex: 1, padding: 24, overflowY: 'auto', display: 'flex', flexDirection: 'column', gap: 16 }}>
                {messages.length > 0 ? (
                  messages.map((msg, i) => {
                    const isMe = msg.senderId === user.id;
                    return (
                      <div key={i} style={{ display: 'flex', justifyContent: isMe ? 'flex-end' : 'flex-start' }}>
                        <div style={{
                          maxWidth: '70%',
                          padding: '12px 16px',
                          borderRadius: 16,
                          borderBottomRightRadius: isMe ? 4 : 16,
                          borderBottomLeftRadius: !isMe ? 4 : 16,
                          background: isMe ? 'var(--primary-600)' : 'rgba(255,255,255,0.1)',
                          color: '#fff',
                          position: 'relative'
                        }}>
                          <div style={{ fontSize: '0.95rem', lineHeight: 1.5, wordWrap: 'break-word', whiteSpace: 'pre-wrap' }}>
                            {msg.content}
                          </div>
                          <div style={{ fontSize: '0.7rem', color: isMe ? 'rgba(255,255,255,0.7)' : 'var(--text-muted)', textAlign: 'right', marginTop: 4 }}>
                            {new Date(msg.sentAt).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}
                          </div>
                        </div>
                      </div>
                    );
                  })
                ) : (
                  <div style={{ margin: 'auto', textAlign: 'center', color: 'var(--text-muted)' }}>
                    <FiMessageSquare size={48} style={{ opacity: 0.3, marginBottom: 16 }} />
                    <p>Démarrez la conversation avec {activeContact.contactName || activeContact.name}</p>
                  </div>
                )}
                <div ref={messagesEndRef} />
              </div>

              {/* Input Area */}
              <div style={{ padding: 16, borderTop: '1px solid rgba(255,255,255,0.06)', background: 'rgba(255,255,255,0.02)' }}>
                <form onSubmit={handleSend} style={{ display: 'flex', gap: 12 }}>
                  <input
                    type="text"
                    value={newMessage}
                    onChange={(e) => setNewMessage(e.target.value)}
                    placeholder="Écrivez votre message..."
                    className="form-input"
                    style={{ flex: 1, borderRadius: 100, paddingLeft: 20 }}
                  />
                  <button 
                    type="submit" 
                    className="btn btn-primary"
                    style={{ borderRadius: 100, width: 44, height: 44, padding: 0, display: 'flex', alignItems: 'center', justifyContent: 'center' }}
                    disabled={!newMessage.trim()}
                  >
                    <FiSend size={18} />
                  </button>
                </form>
              </div>
            </>
          ) : (
            <div style={{ margin: 'auto', textAlign: 'center', color: 'var(--text-muted)' }}>
              <FiMessageSquare size={64} style={{ opacity: 0.2, marginBottom: 20 }} />
              <h2>Vos Messages</h2>
              <p>Sélectionnez une conversation pour commencer à discuter.</p>
            </div>
          )}
        </div>
      </div>
    </div>
  );
}
