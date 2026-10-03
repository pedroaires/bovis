export const getGMDColor = (gmd) => {
    if (gmd === null || gmd === undefined) return 'grey';
    if (gmd >= 1.2) return 'success';
    if (gmd >= 0.8) return 'warning';
    return 'error';
};

export const getGainClass = (ganho) => {
    if (ganho === null || ganho === undefined) return '';
    return ganho >= 0 ? 'text-success' : 'text-error';
}
