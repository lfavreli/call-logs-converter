import { Box, Grid, Typography } from "@mui/material";

const Contact = () => {
    return (
        <Box>
            <Grid container spacing={3}>
                <Grid item xs={12}>
                    <Typography variant="h4" align="center" gutterBottom>
                        Contact Me
                    </Typography>
                </Grid>
                <Grid item xs={12} md={6}>
                    <Typography variant="h6" gutterBottom>
                        About Me
                    </Typography>
                    <Typography variant="body1" paragraph>
                        Add a brief description about myself here. Highlight my skills, experiences, and interests.
                    </Typography>
                </Grid>
                <Grid item xs={12} md={6}>
                    <Typography variant="h6" gutterBottom>
                        Contact Information
                    </Typography>
                    <Typography variant="body1" paragraph>
                        Email: your.email@example.com
                    </Typography>
                    <Typography variant="body1" paragraph>
                        Phone: (123) 456-7890
                    </Typography>
                    <Typography variant="body1" paragraph>
                        Location: City, Country
                    </Typography>
                </Grid>
            </Grid>
        </Box>
    );
}

export default Contact;