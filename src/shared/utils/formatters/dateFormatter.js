export const formatDate = (dateString) => {
    if (!dateString) return '—';
    const date = new Date(dateString + 'T00:00:00'); 
    return date.toLocaleDateString('pt-BR');
};

export const formatDateShort = (dateString) => {
    if (!dateString) return '—';
    // Adiciona T00:00:00 para garantir que a data seja interpretada como local
    const date = new Date(dateString + 'T00:00:00'); 
    return date.toLocaleDateString('pt-BR', { day: '2-digit', month: '2-digit', year: '2-digit' });
};
